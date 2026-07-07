package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.LogisticaClient;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.TransporteClient;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.DepositoClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.TarifaClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.UbicacionClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.integrations.OsrmClient;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.integrations.OsrmClient.OSRMResponse;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Ruta;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Solicitud;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Tramo;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.CoordenadaDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.RutaResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.RutaTentativaDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.TramoDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.TramoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.RutaRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.SolicitudRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RutaService {

    @Autowired
    private OsrmClient osrmClient;

    @Autowired
    private LogisticaClient logisticaClient;

    @Autowired
    private TransporteClient transporteClient;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private RutaRepository rutaRepository;

    private static final double MAX_HOP_DISTANCE_KM_2 = 500.0; // Distancia máxima por tramo (Estrategia 2)
    private static final double MAX_HOP_DISTANCE_KM_3 = 1000.0; // Distancia máxima por tramo (Estrategia 3)

    public List<RutaTentativaDto> generarYGuardarRutasTentativas(Long solicitudId, CoordenadaDto origen,
            CoordenadaDto destino, String ciudadOrigen, String direccionOrigen, String ciudadDestino,
            String direccionDestino) {

        // 1. Calculamos las 3 estrategias
        List<RutaTentativaDto> alternativas = calcularRutaTentativa(origen, destino, ciudadOrigen,
                direccionOrigen, ciudadDestino, direccionDestino);

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + solicitudId));

        TarifaClientDto tarifa = transporteClient.obtenerTarifaPorId(solicitud.getIdTarifa());

        for (RutaTentativaDto alt : alternativas) {
            Ruta ruta = new Ruta();
            ruta.setSolicitud(solicitud);
            ruta.setCantidadTramos(alt.getTramos() != null ? alt.getTramos().size() : 0);

            // Lógica para contar depósitos únicos
            int cantidadDepositos = 0;
            if (alt.getTramos() != null) {
                Set<Long> depositosIds = new HashSet<>();
                for (TramoDto td : alt.getTramos()) {
                    if ("DEPOSITO".equalsIgnoreCase(td.getTipoOrigen()) && td.getIdOrigen() != null) {
                        depositosIds.add(td.getIdOrigen());
                    }
                    if ("DEPOSITO".equalsIgnoreCase(td.getTipoDestino()) && td.getIdDestino() != null) {
                        depositosIds.add(td.getIdDestino());
                    }
                }
                cantidadDepositos = depositosIds.size();
            }
            ruta.setCantidadDepositos(cantidadDepositos);
            ruta.setDistanciaTotalKm(alt.getDistanciaTotalKm());

            // Guardar Tramos
            List<Tramo> tramosEnt = new ArrayList<>();
            if (alt.getTramos() != null) {
                for (TramoDto td : alt.getTramos()) {
                    Tramo t = Tramo.builder()
                            .ruta(ruta)
                            .tipoTramo(Tramo.TipoTramo.valueOf(td.getTipoTramo()))
                            .distanciaKm(td.getDistanciaKm())
                            .idOrigen(td.getIdOrigen()).tipoOrigen(td.getTipoOrigen())
                            .idDestino(td.getIdDestino()).tipoDestino(td.getTipoDestino())
                            .idCamion(td.getIdCamionAsignado()) // Nullable
                            .build();
                    tramosEnt.add(t);
                }
            }
            ruta.setTramos(tramosEnt);

            // Calcular costo usando la distancia total de esta alternativa
            double costoEstimado = calcularCostoEstimado(tarifa, alt.getDistanciaTotalKm(), tramosEnt.size());

            ruta.setTiempoEstimado(alt.getTiempoEstimado());
            ruta.setCostoEstimado(costoEstimado);

            rutaRepository.save(ruta);
        }
        return alternativas;
    }

    /**
     * *
     * CÁLCULO DE LAS 3 ESTRATEGIAS DE RUTA
     */
    public List<RutaTentativaDto> calcularRutaTentativa(CoordenadaDto origen, CoordenadaDto destino,
            String ciudadOrigen, String direccionOrigen, String ciudadDestino, String direccionDestino) {

        List<DepositoClientDto> depositosClient = logisticaClient.obtenerDepositos();
        List<CoordenadaDto> todosLosDepositos = depositosClient.stream()
                .map(d -> new CoordenadaDto(d.getLatitud(), d.getLongitud(), "DEPOSITO"))
                .toList();

        // --- RUTA 1: DIRECTA ---
        RutaTentativaDto rutaDirecta = buildRutaTentativaFromPoints(
                List.of(origen, destino),
                ciudadOrigen, direccionOrigen, ciudadDestino, direccionDestino);

        // --- RUTA 2: DESVÍO (Más estricta) ---
        List<CoordenadaDto> puntosOptimizadosEstrategia2 = generarCaminoOptimizadoEstrategia2(origen, destino,
                todosLosDepositos);

        RutaTentativaDto rutaDesvio = buildRutaTentativaFromPoints(
                puntosOptimizadosEstrategia2, ciudadOrigen, direccionOrigen, ciudadDestino, direccionDestino);

        // --- RUTA 3: OPTIMIZADA (Nueva lógica aplicada) ---
        List<CoordenadaDto> puntosOptimizadosEstrategia3 = generarCaminoOptimizado(origen, destino, todosLosDepositos);

        RutaTentativaDto rutaOptimizada = buildRutaTentativaFromPoints(
                puntosOptimizadosEstrategia3, ciudadOrigen, direccionOrigen, ciudadDestino, direccionDestino);

        return List.of(rutaDirecta, rutaDesvio, rutaOptimizada);
    }

    private List<CoordenadaDto> generarCaminoOptimizadoEstrategia2(CoordenadaDto origen, CoordenadaDto destino,
            List<CoordenadaDto> depositos) {
        List<CoordenadaDto> camino = new ArrayList<>();
        camino.add(origen);

        CoordenadaDto actual = origen;
        Set<CoordenadaDto> visitados = new HashSet<>();
        boolean llegoADestino = false;

        while (!llegoADestino) {
            double distRestanteActual = distanciaTiempo(actual, destino).distanciaKm;

            // Si estamos cerca del destino final (< 200km) vamos directo para evitar
            // paradas innecesarias
            if (distRestanteActual < 200) {
                camino.add(destino);
                llegoADestino = true;
                break;
            }

            CoordenadaDto mejorSiguiente = null;
            // AHORA buscamos minimizar la distancia al DESTINO, no al punto actual
            double mejorDistanciaAlDestino = Double.MAX_VALUE;

            for (CoordenadaDto candidato : depositos) {
                if (visitados.contains(candidato)) {
                    continue;
                }

                DistTiempo saltoMetrics = distanciaTiempo(actual, candidato);
                double distanciaSalto = saltoMetrics.distanciaKm;

                // 1. Validar Hop Máximo (1000km)
                if (distanciaSalto > MAX_HOP_DISTANCE_KM_2) {
                    continue;
                }

                double distCandidatoADestino = distanciaTiempo(candidato, destino).distanciaKm;

                // 2. Validar Progreso: El candidato debe acercarnos al destino
                if (distCandidatoADestino >= distRestanteActual) {
                    continue;
                }

                // 3. CAMBIO CLAVE: Elegimos el que nos deje MÁS CERCA DEL DESTINO FINAL
                // (Esto hará que salte de La Pampa a Rafaela, ignorando Villa María que está
                // "en el medio" pero avanza menos)
                if (distCandidatoADestino < mejorDistanciaAlDestino) {
                    mejorDistanciaAlDestino = distCandidatoADestino;
                    mejorSiguiente = candidato;
                }
            }

            if (mejorSiguiente != null) {
                camino.add(mejorSiguiente);
                visitados.add(mejorSiguiente);
                actual = mejorSiguiente;
            } else {
                // Si no hay intermediarios válidos, salto final
                camino.add(destino);
                llegoADestino = true;
            }
        }
        return camino;
    }

    private List<CoordenadaDto> generarCaminoOptimizado(CoordenadaDto origen, CoordenadaDto destino,
            List<CoordenadaDto> depositos) {
        List<CoordenadaDto> camino = new ArrayList<>();
        camino.add(origen);

        CoordenadaDto actual = origen;
        Set<CoordenadaDto> visitados = new HashSet<>();
        boolean llegoADestino = false;

        while (!llegoADestino) {
            double distRestanteActual = distanciaTiempo(actual, destino).distanciaKm;

            // Si estamos cerca del destino final (< 200km) vamos directo para evitar
            // paradas innecesarias
            if (distRestanteActual < 200) {
                camino.add(destino);
                llegoADestino = true;
                break;
            }

            CoordenadaDto mejorSiguiente = null;
            // AHORA buscamos minimizar la distancia al DESTINO, no al punto actual
            double mejorDistanciaAlDestino = Double.MAX_VALUE;

            for (CoordenadaDto candidato : depositos) {
                if (visitados.contains(candidato)) {
                    continue;
                }

                DistTiempo saltoMetrics = distanciaTiempo(actual, candidato);
                double distanciaSalto = saltoMetrics.distanciaKm;

                // 1. Validar Hop Máximo (1000km)
                if (distanciaSalto > MAX_HOP_DISTANCE_KM_3) {
                    continue;
                }

                double distCandidatoADestino = distanciaTiempo(candidato, destino).distanciaKm;

                // 2. Validar Progreso: El candidato debe acercarnos al destino
                if (distCandidatoADestino >= distRestanteActual) {
                    continue;
                }

                // 3. CAMBIO CLAVE: Elegimos el que nos deje MÁS CERCA DEL DESTINO FINAL
                // (Esto hará que salte de La Pampa a Rafaela, ignorando Villa María que está
                // "en el medio" pero avanza menos)
                if (distCandidatoADestino < mejorDistanciaAlDestino) {
                    mejorDistanciaAlDestino = distCandidatoADestino;
                    mejorSiguiente = candidato;
                }
            }

            if (mejorSiguiente != null) {
                camino.add(mejorSiguiente);
                visitados.add(mejorSiguiente);
                actual = mejorSiguiente;
            } else {
                // Si no hay intermediarios válidos, salto final
                camino.add(destino);
                llegoADestino = true;
            }
        }
        return camino;
    }

    // Construye el DTO iterando la lista de puntos
    private RutaTentativaDto buildRutaTentativaFromPoints(List<CoordenadaDto> puntos, String ciudadOrigen,
            String direccionOrigen, String ciudadDestino, String direccionDestino) {

        List<TramoDto> tramos = new ArrayList<>();
        double distanciaTotal = 0.0;
        double tiempoTotal = 0.0;

        for (int i = 0; i < puntos.size() - 1; i++) {
            CoordenadaDto p1 = puntos.get(i);
            CoordenadaDto p2 = puntos.get(i + 1);

            DistTiempo dt = distanciaTiempo(p1, p2);
            TipoTramo tipo = determinarTipoTramo(p1.getTipo(), p2.getTipo());

            // Helpers para obtener IDs limpiamente
            Long idOrigen = obtenerIdUbicacion(p1, ciudadOrigen, direccionOrigen);
            Long idDestino = obtenerIdUbicacion(p2, ciudadDestino, direccionDestino);

            tramos.add(TramoDto.builder()
                    .tipoTramo(tipo.name())
                    .distanciaKm(redondear2(dt.distanciaKm))
                    .tiempoHoras(redondear2(dt.tiempoHoras))
                    .idOrigen(idOrigen)
                    .tipoOrigen("DEPOSITO".equalsIgnoreCase(p1.getTipo()) ? "DEPOSITO" : "ORIGEN")
                    .idDestino(idDestino)
                    .tipoDestino("DEPOSITO".equalsIgnoreCase(p2.getTipo()) ? "DEPOSITO" : "DESTINO")
                    .build());

            distanciaTotal += dt.distanciaKm;
            tiempoTotal += dt.tiempoHoras;
        }

        return RutaTentativaDto.builder()
                .tramos(tramos)
                .distanciaTotalKm(redondear2(distanciaTotal))
                .tiempoEstimado(redondear2(tiempoTotal))
                .cantidadTramos(tramos.size())
                .cantidadDepositos(Math.max(0, tramos.size() - 1))
                .build();
    }

    // Helper para simplificar la obtención de IDs dentro del loop
    private Long obtenerIdUbicacion(CoordenadaDto punto, String ciudadRef, String dirRef) {
        if ("DEPOSITO".equalsIgnoreCase(punto.getTipo())) {
            DepositoClientDto d = logisticaClient.buscarDepositoPorCoordenadas(punto.getLat(), punto.getLon());
            return d != null ? d.getId() : null;
        } else {
            // Si no es deposito, es origen o destino del cliente
            UbicacionClientDto u = logisticaClient.buscarUbicacionPorCoordenadas(punto.getLat(), punto.getLon(),
                    ciudadRef, dirRef);
            return u != null ? u.getId() : null;
        }
    }

    private DistTiempo distanciaTiempo(CoordenadaDto a, CoordenadaDto b) {
        try {
            OSRMResponse res = osrmClient.consultarRuta(a.getLat(), a.getLon(), b.getLat(), b.getLon());
            if (res != null && !res.getRoutes().isEmpty()) {
                OSRMResponse.Route r = res.getRoutes().get(0);
                return new DistTiempo(r.getDistance() / 1000.0, r.getDuration() / 3600.0);
            }
        } catch (Exception e) {
            // Logear error si es necesario
        }
        // Fallback básico si falla OSRM o no hay ruta
        return calcularDistanciaEuclidiana(a, b);
    }

    // Fallback matemático simple por si OSRM falla
    private DistTiempo calcularDistanciaEuclidiana(CoordenadaDto a, CoordenadaDto b) {
        double R = 6371;
        double dLat = Math.toRadians(b.getLat() - a.getLat());
        double dLon = Math.toRadians(b.getLon() - a.getLon());
        double x = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(a.getLat())) * Math.cos(Math.toRadians(b.getLat())) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));
        double dist = R * c;
        return new DistTiempo(dist, dist / 80.0); // Asumiendo 80km/h
    }

    private TipoTramo determinarTipoTramo(String tipoA, String tipoB) {
        boolean aEsDep = "DEPOSITO".equalsIgnoreCase(tipoA);
        boolean bEsDep = "DEPOSITO".equalsIgnoreCase(tipoB);
        if (!aEsDep && bEsDep) {
            return TipoTramo.ORIGEN_DEPOSITO;
        }
        if (aEsDep && bEsDep) {
            return TipoTramo.DEPOSITO_DEPOSITO;
        }
        if (aEsDep && !bEsDep) {
            return TipoTramo.DEPOSITO_DESTINO;
        }
        return TipoTramo.ORIGEN_DESTINO;
    }

    private double redondear2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // Records y Enums internos
    private record DistTiempo(double distanciaKm, double tiempoHoras) {

    }

    public enum TipoTramo {
        ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, DEPOSITO_DESTINO, ORIGEN_DESTINO
    }

    public RutaResponseDto consultarRuta(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Buscar la ruta seleccionada por la solicitud (si existe)
        Ruta rutaSeleccionada;
        if (solicitud.getIdRutaSeleccionada() != null) {
            rutaSeleccionada = rutaRepository.findById(solicitud.getIdRutaSeleccionada())
                    .orElseThrow(() -> {
                        log.error("La solicitud no tiene ruta seleccionada");
                        throw new RuntimeException("No se ha seleccionado una ruta para la solicitud");
                    });
        } else {
            List<Ruta> rutas = rutaRepository.findAllBySolicitudId(solicitud.getId());
            if (rutas == null || rutas.isEmpty()) {
                log.error("La solicitud no tiene ruta seleccionada");
                throw new RuntimeException("La solicitud no tiene ruta asignada");
            }
            log.error("La solicitud no tiene ruta seleccionada");
            throw new RuntimeException("No se ha seleccionado una ruta para la solicitud");
        }

        List<Tramo> tramos = rutaSeleccionada.getTramos();

        List<TramoResponseDto> tramosDto = tramos.stream()
                .map(t -> TramoResponseDto.builder()
                        .idTramo(t.getId())
                        .idRuta(rutaSeleccionada.getId())
                        .tipoTramo(t.getTipoTramo().name())
                        .distanciaKm(t.getDistanciaKm())
                        .fechaHoraInicio(t.getFechaHoraInicio())
                        .fechaHoraFin(t.getFechaHoraFin())
                        .idOrigen(t.getIdOrigen())
                        .tipoOrigen(t.getTipoOrigen())
                        .idDestino(t.getIdDestino())
                        .tipoDestino(t.getTipoDestino())
                        .idCamion(t.getIdCamion())
                        .build())
                .toList();

        return RutaResponseDto.builder()
                .idRuta(rutaSeleccionada.getId())
                .idSolicitud(rutaSeleccionada.getSolicitud().getId())
                .cantidadTramos(rutaSeleccionada.getTramos().size())
                .cantidadDepositos(rutaSeleccionada.getCantidadDepositos())
                .tramos(tramosDto)
                .distanciaTotalKm(rutaSeleccionada.getDistanciaTotalKm())
                .tiempoEstimado(rutaSeleccionada.getTiempoEstimado())
                .costoEstimado(rutaSeleccionada.getCostoEstimado())
                .build();
    }

    public List<RutaTentativaDto> obtenerRutasTentativas(Long idSolicitud) {

        List<Ruta> rutas = rutaRepository.findAllBySolicitudId(idSolicitud);

        return rutas.stream()
                .map(r -> {
                    List<TramoDto> tramosDto = r.getTramos() != null ? r.getTramos()
                            .stream()
                            .map(t -> TramoDto.builder()
                                    .tipoTramo(t.getTipoTramo().name())
                                    .distanciaKm(t.getDistanciaKm())
                                    .tiempoHoras(t.getDistanciaKm() / 60.0)
                                    .idCamionAsignado(t.getIdCamion())
                                    .idOrigen(t.getIdOrigen())
                                    .tipoOrigen(t.getTipoOrigen())
                                    .idDestino(t.getIdDestino())
                                    .tipoDestino(t.getTipoDestino())
                                    .build())
                            .toList() : List.of();

                    return RutaTentativaDto.builder()
                            .tramos(tramosDto)
                            .distanciaTotalKm(r.getDistanciaTotalKm())
                            .tiempoEstimado(r.getTiempoEstimado())
                            .costoEstimado(r.getCostoEstimado())
                            .cantidadTramos(r.getTramos() != null ? r.getTramos().size()
                                    : 0)
                            .cantidadDepositos(r.getCantidadDepositos())
                            .idRuta(r.getId())
                            .build();
                })
                .toList();

    }

    /**
     * *
     * ASIGNACION DE RUTA A SOLICITUD
     */
    public RutaResponseDto seleccionarRutaParaSolicitud(Long solicitudId, Long rutaId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException(
                        "Solicitud no encontrada con id: " + solicitudId));

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + rutaId));

        if (ruta.getSolicitud() == null || !ruta.getSolicitud().getId().equals(solicitudId)) {
            throw new RuntimeException("La ruta no pertenece a la solicitud indicada");
        }
        if (solicitud.getEstado() != Solicitud.EstadoSolicitud.BORRADOR) {
            log.error("Solo se pueden asignar rutas a solicitudes en estado BORRADOR (no inicaiadas)");
            throw new RuntimeException("Solo se pueden asignar rutas a solicitudes en estado BORRADOR (no inicaiadas)");
        }

        solicitud.setIdRutaSeleccionada(ruta.getId());
        solicitud.setEstado(Solicitud.EstadoSolicitud.PROGRAMADA);

        // Setear tiempo y costo estimado
        solicitud.setTiempoEstimado(ruta.getTiempoEstimado());
        solicitud.setCostoEstimado(ruta.getCostoEstimado());
        solicitudRepository.save(solicitud);

        return RutaResponseDto.builder()
                .idRuta(ruta.getId())
                .idSolicitud(solicitud.getId())
                .cantidadTramos(ruta.getTramos() != null ? ruta.getTramos().size() : 0)
                .cantidadDepositos(ruta.getCantidadDepositos())
                .distanciaTotalKm(ruta.getDistanciaTotalKm())
                .costoEstimado(ruta.getCostoEstimado())
                .tiempoEstimado(ruta.getTiempoEstimado())
                .tramos(ruta.getTramos() != null ? ruta.getTramos().stream().map(t -> TramoResponseDto
                        .builder()
                        .idTramo(t.getId()).idRuta(ruta.getId())
                        .tipoTramo(t.getTipoTramo().name())
                        .distanciaKm(t.getDistanciaKm()).fechaHoraInicio(t.getFechaHoraInicio())
                        .fechaHoraFin(t.getFechaHoraFin()).idOrigen(t.getIdOrigen())
                        .tipoOrigen(t.getTipoOrigen())
                        .idDestino(t.getIdDestino()).tipoDestino(t.getTipoDestino())
                        .idCamion(t.getIdCamion())
                        .build()).toList() : null)
                .build();
    }

    public double calcularCostoEstimado(TarifaClientDto tarifa, double distanciaTotalKm, int cantTramos) {

        double cargosGestionPorTramo = 200; // Valor fijo configurable (debe coincidir con el
                                            // cargosGestionPorTramo del costoFinal)

        double cargos = cantTramos * cargosGestionPorTramo;

        // Deberia incluir los cargosGestionPorTramo??
        double costoCombustible = tarifa.getConsumoCombustibleGralAprox()
                * tarifa.getCostoCombustibleLitro()
                * distanciaTotalKm;

        double costoBase = tarifa.getCostoBaseKm() * distanciaTotalKm;

        return costoCombustible + costoBase + cargos;
    }

}
