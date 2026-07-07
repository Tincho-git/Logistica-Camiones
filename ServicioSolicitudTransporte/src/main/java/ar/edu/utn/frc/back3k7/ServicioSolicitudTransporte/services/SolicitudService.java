package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services;

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
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Cliente;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Contenedor;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Ruta;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Solicitud;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Tramo;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.CoordenadaDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.SolicitudDetalleDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.SolicitudEstadoDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.SolicitudRequestDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.SolicitudResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.TramoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.ClienteRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.ContenedorRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.RutaRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.SolicitudRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.TramoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SolicitudService {

        @Autowired
        private SolicitudRepository solicitudRepository;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private ContenedorRepository contenedorRepository;

        @Autowired
        private RutaService rutaService;

        @Autowired
        private TransporteClient transporteClient;

        @Autowired
        private LogisticaClient logisticaClient;

        @Autowired
        private RutaRepository rutaRepository;

        @Autowired
        private TramoRepository tramoRepository;

        public Solicitud toEntity(SolicitudRequestDto requestDto) {

                // Buscar cliente por DNI
                Cliente cliente = clienteRepository.findByDni(requestDto.getDni())
                                .orElseGet(() -> clienteRepository.save(
                                                Cliente.builder()
                                                                .dni(requestDto.getDni())
                                                                .tipoDni(requestDto.getTipoDni().charAt(0))
                                                                .nombre(requestDto.getNombre())
                                                                .apellido(requestDto.getApellido())
                                                                .telefono(requestDto.getTelefono())
                                                                .email(requestDto.getEmail())
                                                                .build()));

                // Crear Contenedor
                Contenedor contenedor = Contenedor.builder()
                                .ancho(requestDto.getAncho())
                                .largo(requestDto.getLargo())
                                .altura(requestDto.getAltura())
                                .peso(requestDto.getPeso())
                                .cliente(cliente)
                                .build();

                double pesoContenedor = contenedor.getPeso();
                double volumenContenedor = contenedor.getVolumen();

                List<TarifaClientDto> tarifas = transporteClient.obtenerTodasLasTarifas();
                // Filtrar la tarifa que cumpla con las medidas del contenedor
                TarifaClientDto tarifaSeleccionada = tarifas.stream()
                                .filter(t -> pesoContenedor >= t.getRangoPesoMin()
                                                && pesoContenedor <= t.getRangoPesoMax()
                                                && volumenContenedor >= t.getRangoVolumenMin()
                                                && volumenContenedor <= t.getRangoVolumenMax())
                                .findFirst()
                                .orElseThrow(() -> {
                                        log.error("No hay tarifa válida para el contenedor {}", contenedor.getId());
                                        return new RuntimeException();
                                });

                return Solicitud.builder()
                                .cliente(cliente)
                                .contenedor(contenedor)
                                .fechaHoraInicio(LocalDateTime.now())
                                .idTarifa(tarifaSeleccionada.getId())
                                .direccionOrigen(requestDto.getDireccionOrigen())
                                .direccionDestino(requestDto.getDireccionDestino())
                                .build();
        }

        public SolicitudResponseDto toResponseDto(Solicitud entity) {

                return SolicitudResponseDto.builder()
                                .idSolicitud(entity.getId())
                                .fechaHoraInicio(entity.getFechaHoraInicio())
                                .fechaHoraFin(entity.getFechaHoraFin())
                                .costoEstimado(entity.getCostoEstimado())
                                .tiempoEstimado(entity.getTiempoEstimado())
                                .costoFinal(entity.getCostoFinal())
                                .tiempoReal(entity.getTiempoReal())
                                .estado(entity.getEstado())
                                .clienteDni(entity.getCliente().getDni())
                                .idContenedor(entity.getContenedor().getId())
                                .direccionOrigen(entity.getDireccionOrigen())
                                .direccionDestino(entity.getDireccionDestino())
                                .tarifa(transporteClient.obtenerTarifaPorId(entity.getIdTarifa()))
                                .build();
        }

        /**
         * *
         * Recibe solicitudes de traslado de contenedores desde un punto de origen a
         * un terreno o ubicación de destino.
         * 
         * . Registrar una nueva solicitud de transporte de contenedor. (Cliente) .
         * La solicitud incluye la creación del contenedor con su identificación
         * única . La solicitud incluye el registro del cliente si no existe
         * previamente . Las solicitudes deben registrar un estado, por ejemplo:
         * [borrador - programada - en tránsito entregada]
         *
         */
        public SolicitudResponseDto crearSolicitud(SolicitudRequestDto dto) {

                Solicitud solicitud = toEntity(dto);

                contenedorRepository.save(solicitud.getContenedor());

                solicitudRepository.save(solicitud);

                // Generar ruta tentativa usando coordenadas del dto
                CoordenadaDto origen = new CoordenadaDto(dto.getLatOrigen(), dto.getLonOrigen(), dto.getTipoOrigen());
                CoordenadaDto destino = new CoordenadaDto(dto.getLatDestino(), dto.getLonDestino(),
                                dto.getTipoDestino());

                String ciudadOrigen = dto.getCiudadOrigen();
                String direccionOrigen = dto.getDireccionOrigen();

                String ciudadDestino = dto.getCiudadDestino();
                String direccionDestino = dto.getDireccionDestino();

                // Generar y persistir 3 rutas tentativas en BD asociadas a la solicitud recién
                // creada.
                rutaService.generarYGuardarRutasTentativas(solicitud.getId(), origen, destino, ciudadOrigen,
                                direccionOrigen, ciudadDestino, direccionDestino);

                return toResponseDto(solicitud);
        }

        public SolicitudDetalleDto obtenerSolicitud(Long id) {
                Solicitud solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Solicitud {} no encontrada", id);
                                        return new RuntimeException("Solicitud no encontrada");
                                });

                Contenedor contenedor = solicitud.getContenedor();
                Cliente cliente = solicitud.getCliente();

                Ruta ruta;
                if (solicitud.getIdRutaSeleccionada() != null) {
                        ruta = rutaRepository.findById(solicitud.getIdRutaSeleccionada())
                                        .orElseThrow(() -> {
                                                log.error("Ruta {} no encontrada", solicitud.getIdRutaSeleccionada());
                                                return new RuntimeException("Ruta no encontrada");
                                        });
                } else {
                        List<Ruta> rutas = rutaRepository.findAllBySolicitudId(id);
                        if (rutas == null || rutas.isEmpty()) {
                                log.error("La solicitud {} no tiene ruta asignada", id);
                                throw new RuntimeException();
                        }
                        log.error("La solicitud {} no tiene ruta asignada", id);
                        throw new RuntimeException();
                }

                return SolicitudDetalleDto.builder()
                                .idSolicitud(solicitud.getId())
                                .estado(solicitud.getEstado().name())
                                .idContenedor(contenedor.getId())
                                .estadoContenedor(contenedor.getEstado().toString())
                                .ancho(contenedor.getAncho())
                                .largo(contenedor.getLargo())
                                .peso(contenedor.getPeso())
                                .altura(contenedor.getAltura())
                                .clienteNombre(cliente.getNombre())
                                .clienteApellido(cliente.getApellido())
                                .clienteDni(cliente.getDni())
                                .clienteTelefono(cliente.getTelefono())
                                .clienteEmail(cliente.getEmail())
                                .costoEstimado(solicitud.getCostoEstimado())
                                .costoFinal(solicitud.getCostoFinal())
                                .tiempoEstimado(solicitud.getTiempoEstimado())
                                .tiempoReal(solicitud.getTiempoReal() != 0 ? solicitud.getTiempoReal()
                                                : 0)
                                .idTarifa(solicitud.getIdTarifa())
                                .cantidadTramos(ruta.getTramos().size())
                                .idEstadiasDepositos(solicitud.getIdEstadiasDepositos())
                                .build();
        }

        public SolicitudEstadoDto obtenerEstado(Long id) {
                Solicitud solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Solicitud {} no encontrada", id);
                                        return new RuntimeException("Solicitud no encontrada");
                                });

                return SolicitudEstadoDto.builder()
                                .idSolicitud(solicitud.getId())
                                .estado(solicitud.getEstado().name())
                                .build();
        }

        public List<SolicitudResponseDto> getAll() {
                List<Solicitud> solicitudes = solicitudRepository.findAll();
                return solicitudes.stream()
                                .map(solicitud -> toResponseDto(solicitud))
                                .toList();
        }

        public TramoResponseDto asignarCamion(Long idSolicitud, Long idCamion) {

                Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                                .orElseThrow(() -> {
                                        log.error("Solicitud {} no encontrada", idSolicitud);
                                        return new RuntimeException("Solicitud no encontrada");
                                });

                CamionClientDto camion = transporteClient.obtenerCamionPorId(idCamion);

                if (solicitud.getContenedor().getPeso() > camion.getPeso() ||
                                solicitud.getContenedor().getVolumen() > camion.getVolumen()) {
                        log.error("El camion {} no puede transportar el contenedor {}", idCamion,
                                        solicitud.getContenedor().getId());
                        throw new RuntimeException();
                }

                // Si la solicitud tiene una ruta seleccionada preferirla, si no buscar entre
                // las rutas tentativas
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
                                log.error("La solicitud {} no tiene ruta asignada", idSolicitud);
                                throw new RuntimeException();
                        }

                        log.error("La solicitud {} no tiene ruta asignada", idSolicitud);
                        throw new RuntimeException();
                }

                Tramo tramo = ruta.getTramos()
                                .stream()
                                .filter(t -> t.getFechaHoraFin() == null)
                                .findFirst()
                                .orElseThrow(() -> {
                                        log.error("Todos los tramos se encuentran finalizados");
                                        return new RuntimeException();
                                });

                if (tramo.getFechaHoraInicio() == null) {
                        tramo.setIdCamion(idCamion);
                } else {
                        log.error("Este tramo ya comenzo");
                        throw new RuntimeException();
                }

                tramoRepository.save(tramo);

                transporteClient.actualizarCamion(camion);

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

        /**
         * 
         * La tarifa final del envío se calcula como:
         * 
         * Cargos de Gestión valor fijo en base a la cantidad de tramos
         * + costo por kilómetro de cada camión
         * + costo de combustible calculado como
         * (consumo del camión en el tramo × valor del litro)
         * + costo por estadía en depósito (por día)
         * 
         */
        public double calcularCostoFinal(Solicitud solicitud, TarifaClientDto tarifa) {
                double costoFinal = 0.0;

                double cargosGestionPorTramo = 200; // Valor fijo configurable (debe coincidir con el
                                                    // cargosGestionPorTramo del costoEstimado)

                // Obtener ruta asociada a la solicitud
                Ruta ruta = rutaRepository.findById(solicitud.getIdRutaSeleccionada())
                                .orElseThrow(() -> {
                                        log.error("Ruta {} no encontrada", solicitud.getIdRutaSeleccionada());
                                        return new RuntimeException("Ruta no encontrada");
                                });

                // Cargos de Gestión valor fijo en base a la cantidad de tramos
                costoFinal += ruta.getCantidadTramos() * cargosGestionPorTramo;

                // Costo por kilómetro de cada camión + Costo de combustible
                for (Tramo tramo : ruta.getTramos()) {
                        CamionClientDto camion = transporteClient.obtenerCamionPorId(tramo.getIdCamion());
                        double distanciaKmTramo = tramo.getDistanciaKm();

                        double costoKm = camion.getCostoBaseKm() * distanciaKmTramo;

                        double costoCombustible = distanciaKmTramo
                                        * camion.getConsumoCombusProm()
                                        * tarifa.getCostoCombustibleLitro();

                        costoFinal += costoKm + costoCombustible;
                }

                // Costo por estadía en depósito (por día)
                if (solicitud.getIdEstadiasDepositos() != null) {
                        for (Long idEstadia : solicitud.getIdEstadiasDepositos()) {
                                long cantDias = logisticaClient.obtenerCantidadDiasEstadia(idEstadia);

                                EstadiaDepositoClientDto estadia = logisticaClient
                                                .buscarEstadiaDepositoPorId(idEstadia);
                                DepositoClientDto deposito = logisticaClient
                                                .buscarDepositoPorId(estadia.getIdDeposito());

                                double costoEstadia = cantDias * deposito.getCostoEstadiaDiaria();

                                costoFinal += costoEstadia;
                        }
                }

                return costoFinal;
        }
}
