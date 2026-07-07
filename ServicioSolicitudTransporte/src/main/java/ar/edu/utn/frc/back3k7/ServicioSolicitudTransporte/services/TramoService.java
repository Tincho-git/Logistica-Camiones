package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.LogisticaClient;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.TransporteClient;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.CamionClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.DepositoClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.EstadiaDepositoClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.TarifaClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Contenedor;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Ruta;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Solicitud;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Tramo;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.TramoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.ContenedorRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.RutaRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.SolicitudRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.TramoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TramoService {

    @Autowired
    private TramoRepository tramoRepository;

    @Autowired
    private ContenedorRepository contenedorRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private LogisticaClient logisticaClient;

    @Autowired
    private TransporteClient transporteClient;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private SolicitudService solicitudService;

    public TramoResponseDto registrarInicio(Long idSolicitud) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> {
                    log.error("Solicitud {} no encontrada", idSolicitud);
                    return new RuntimeException("Solicitud no encontrada");
                });

        // Preferir la ruta seleccionada en la solicitud si existe, sino tomar la
        // primera ruta tentativa
        Ruta ruta;
        if (solicitud.getIdRutaSeleccionada() != null) {
            ruta = rutaRepository.findById(solicitud.getIdRutaSeleccionada())
                    .orElseThrow(() -> {
                        log.error("Ruta {} no encontrada para la solicitud {}", solicitud.getIdRutaSeleccionada(),
                                idSolicitud);
                        return new RuntimeException("Ruta no encontrada para la solicitud");
                    });
        } else {
            List<Ruta> rutas = rutaRepository.findAllBySolicitudId(idSolicitud);
            if (rutas == null || rutas.isEmpty()) {
                log.error("La solicitud {} no tiene rutas asignadas", idSolicitud);
                throw new RuntimeException("Ruta no encontrada para la solicitud");
            }
            log.error("La solicitud {} no tiene rutas asignadas", idSolicitud);
            throw new RuntimeException("Ruta no encontrada para la solicitud");
        }

        Tramo tramo = ruta.getTramos().stream()
                .filter(t -> t.getFechaHoraInicio() == null)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No hay tramos pendientes por iniciar para la solicitud {}", idSolicitud);
                    return new RuntimeException();
                });

        // Validar que el tramo tenga un camión asignado
        if (tramo.getIdCamion() == null) {
            log.error("El tramo con id {} no tiene un camión asignado", tramo.getId());
            throw new RuntimeException();
        }

        // Setear disponibilidad camion False
        CamionClientDto camion = transporteClient.obtenerCamionPorId(tramo.getIdCamion());

        if (camion.isDisponibilidad()) {
            camion.setDisponibilidad(false);
            transporteClient.actualizarCamion(camion);
        } else {
            log.error("El Camion {} no esta disponible", tramo.getIdCamion());
            throw new RuntimeException();
        }

        tramo.setFechaHoraInicio(LocalDateTime.now());
        tramoRepository.save(tramo);

        Contenedor contenedor = tramo.getRuta().getSolicitud().getContenedor();

        // Setea FechaHoraFin a la EstadiaDeposito
        Long idOrigen = tramo.getIdOrigen();
        String tipoOrigen = tramo.getTipoOrigen();

        // Verifica si el origen del tramo es un depósito y si el contenedor está en
        // estado
        // EN_DEPOSITO y no EN_ORIGEN
        if (tipoOrigen.equals("DEPOSITO")) {
            DepositoClientDto depositoOrigen = logisticaClient.buscarDepositoPorId(idOrigen);

            for (Long idEstadia : depositoOrigen.getIdEstadias()) {
                EstadiaDepositoClientDto estadia = logisticaClient
                        .buscarEstadiaDepositoPorId(idEstadia);
                if (estadia.getFechaHoraSalida() == null && estadia.getIdSolicitud().equals(idSolicitud)) {
                    estadia.setFechaHoraSalida(tramo.getFechaHoraInicio().toString());
                    // Actualiza la estadía en el servicio de Logística
                    logisticaClient.actualizarEstadiaDeposito(estadia);
                    break;
                }
            }
        } else {
            solicitud.setEstado(Solicitud.EstadoSolicitud.EN_TRANSITO);
            solicitudRepository.save(solicitud);
        }

        // Cambia el estado del contenedor a EN_VIAJE
        contenedor.setEstado(Contenedor.EstadoContenedor.EN_VIAJE);
        contenedor.getHistorialEstados().add(Contenedor.EstadoContenedor.EN_VIAJE.name());

        contenedorRepository.save(contenedor);

        return TramoResponseDto.builder()
                .idTramo(tramo.getId())
                .idRuta(tramo.getRuta().getId())
                .tipoTramo(tramo.getTipoTramo().name())
                .distanciaKm(tramo.getDistanciaKm())
                .fechaHoraInicio(tramo.getFechaHoraInicio())
                .fechaHoraFin(tramo.getFechaHoraFin())
                .idOrigen(tramo.getIdOrigen())
                .tipoOrigen(tramo.getTipoOrigen())
                .idDestino(tramo.getIdDestino())
                .tipoDestino(tramo.getTipoDestino())
                .idCamion(tramo.getIdCamion())
                .build();
    }

    public TramoResponseDto registrarFin(Long idSolicitud) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> {
                    log.error("Solicitud {} no encontrada", idSolicitud);
                    return new RuntimeException("Solicitud no encontrada");
                });

        // Preferir la ruta seleccionada en la solicitud si existe, sino tomar la
        // primera ruta tentativa
        Ruta ruta;
        if (solicitud.getIdRutaSeleccionada() != null) {
            ruta = rutaRepository.findById(solicitud.getIdRutaSeleccionada())
                    .orElseThrow(() -> {
                        log.error("Ruta {} no encontrada", solicitud.getIdRutaSeleccionada());
                        return new RuntimeException("Ruta no encontrada");
                    });
        } else {
            List<Ruta> rutas = rutaRepository.findAllBySolicitudId(idSolicitud);
            if (rutas == null || rutas.isEmpty()) {
                log.error("La solicitud {} no tiene rutas asignadas", idSolicitud);
                throw new RuntimeException("Ruta no encontrada para la solicitud");
            }
            log.error("La solicitud {} no tiene rutas asignadas", idSolicitud);
            throw new RuntimeException("Ruta no encontrada para la solicitud");
        }

        Tramo tramo = ruta.getTramos().stream()
                .filter(t -> t.getFechaHoraFin() == null)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No hay tramos pendientes por iniciar para la solicitud {}", idSolicitud);
                    return new RuntimeException();
                });

        // No se puede finalizar si no fue iniciado
        if (tramo.getFechaHoraInicio() == null) {
            log.error("El tramo con id {} no puede finalizarse porque nunca fue iniciado", tramo.getId());
            throw new RuntimeException();
        }

        // Marca el fechaHoraFin del tramo con el timestamp actual
        tramo.setFechaHoraFin(LocalDateTime.now());
        tramoRepository.save(tramo);

        CamionClientDto camion = transporteClient.obtenerCamionPorId(tramo.getIdCamion());
        camion.setDisponibilidad(true); // setea disponibilidad=true
        transporteClient.actualizarCamion(camion); // setea disponibilidad=false en MS Transporte

        // Setea FechaHoraEntrada a la EstadiaDeposito
        Long idDestino = tramo.getIdDestino();
        String tipoDestino = tramo.getTipoDestino();

        if (tipoDestino.equals("DEPOSITO")) {
            EstadiaDepositoClientDto estadiaDeposito = logisticaClient
                    .crearEstadiaDeposito(EstadiaDepositoClientDto.builder()
                            .idDeposito(idDestino)
                            .fechaHoraEntrada(tramo.getFechaHoraFin().toString())
                            .idSolicitud(idSolicitud)
                            .build());
            solicitud.getIdEstadiasDepositos().add(estadiaDeposito.getId());

        } else {
            solicitud.setFechaHoraFin(LocalDateTime.now());
            solicitud.setEstado(Solicitud.EstadoSolicitud.ENTREGADA);

            Duration duracion = Duration.between(solicitud.getFechaHoraInicio(), solicitud.getFechaHoraFin());

            double horas = duracion.toHours();
            double minutos = duracion.toMinutes();
            double segundos = duracion.getSeconds();

            // Calcular tiempoReal
            double total = horas + minutos / 60 + segundos / 3600;

            // Buscar la tarifa asociada a la solicitud
            TarifaClientDto tarifa = transporteClient.obtenerTarifaPorId(solicitud.getIdTarifa());

            // Calcular Costo Final
            double costoFinal = solicitudService.calcularCostoFinal(solicitud, tarifa);

            // Setear tiempoReal y costoFinal
            solicitud.setTiempoReal(total);
            solicitud.setCostoFinal(costoFinal);

            solicitudRepository.save(solicitud);

        }

        List<Tramo> tramos = ruta.getTramos();

        boolean esUltimo = tramos.stream()
                .allMatch(t -> t.getFechaHoraFin() != null);

        // Si es el último tramo → cambia el estado del contenedor a ENTREGADO
        // Si no → contenedor pasa a EN_DEPOSITO
        Contenedor contenedor = ruta.getSolicitud().getContenedor();
        if (esUltimo) {
            contenedor.setEstado(Contenedor.EstadoContenedor.ENTREGADO);
            contenedor.getHistorialEstados().add(Contenedor.EstadoContenedor.ENTREGADO.name());
        } else {
            contenedor.setEstado(Contenedor.EstadoContenedor.EN_DEPOSITO);
            contenedor.getHistorialEstados().add(Contenedor.EstadoContenedor.EN_DEPOSITO.name());
        }
        contenedorRepository.save(contenedor);

        return TramoResponseDto.builder()
                .idTramo(tramo.getId())
                .idRuta(tramo.getRuta().getId())
                .tipoTramo(tramo.getTipoTramo().name())
                .distanciaKm(tramo.getDistanciaKm())
                .fechaHoraInicio(tramo.getFechaHoraInicio())
                .fechaHoraFin(tramo.getFechaHoraFin())
                .idOrigen(tramo.getIdOrigen())
                .tipoOrigen(tramo.getTipoOrigen())
                .idDestino(tramo.getIdDestino())
                .tipoDestino(tramo.getTipoDestino())
                .idCamion(tramo.getIdCamion())
                .build();
    }

    public List<TramoResponseDto> obtenerTramosPorTransportista(Long idTransportista) {
        // 1. Obtener el camión del transportista desde MS Transporte
        CamionClientDto camion = transporteClient.obtenerCamionPorTransportista(idTransportista);

        // 2. Buscar tramos asignados a ese camión
        List<Tramo> tramos = tramoRepository.findByIdCamion(camion.getId());

        // 3. Mapear a DTO
        return tramos.stream()
                .map(t -> TramoResponseDto.builder()
                .idTramo(t.getId())
                .idRuta(t.getRuta().getId())
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
    }

}
