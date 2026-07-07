package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Cliente;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Contenedor;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Ruta;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Solicitud;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ContenedorEstadoDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ContenedorRequestDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ContenedorResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.ClienteRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.ContenedorRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.RutaRepository;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.SolicitudRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContenedorService {

        @Autowired
        private ContenedorRepository contenedorRepository;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private SolicitudRepository solicitudRepository;

        @Autowired
        private RutaRepository rutaRepository;

        public Contenedor toEntity(ContenedorRequestDto contenedor, Cliente cliente) {
                return Contenedor.builder()
                                .ancho(contenedor.getAncho())
                                .largo(contenedor.getLargo())
                                .peso(contenedor.getPeso())
                                .altura(contenedor.getAltura())
                                .cliente(cliente)
                                .build();
        }

        public ContenedorResponseDto toResponseDto(Contenedor contenedor) {
                return ContenedorResponseDto.builder()
                                .id(contenedor.getId())
                                .ancho(contenedor.getAncho())
                                .largo(contenedor.getLargo())
                                .peso(contenedor.getPeso())
                                .altura(contenedor.getAltura())
                                .cliente(contenedor.getCliente().getNombre())
                                .build();
        }

        public List<ContenedorResponseDto> getAll() {
                return contenedorRepository.findAll().stream()
                                .map(this::toResponseDto)
                                .collect(Collectors.toList());
        }

        public ContenedorResponseDto create(ContenedorRequestDto dto) {

                Cliente clienteAsociado = clienteRepository.findById(dto.getCliente())
                                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

                Contenedor nuevoContenedor = toEntity(dto, clienteAsociado);

                Contenedor contenedorGuardado = contenedorRepository.save(nuevoContenedor);

                return toResponseDto(contenedorGuardado);

        }

        public ContenedorResponseDto actualizarContenedor(Long idContenedor, ContenedorRequestDto dto) {
                Contenedor contenedorExistente = contenedorRepository.findById(idContenedor)
                                .orElseThrow(() -> new RuntimeException(
                                                "Contenedor no encontrado con id: " + idContenedor));

                // Actualizar los campos del contenedor existente con los valores del DTO
                contenedorExistente.setAncho(dto.getAncho());
                contenedorExistente.setLargo(dto.getLargo());
                contenedorExistente.setPeso(dto.getPeso());
                contenedorExistente.setAltura(dto.getAltura());

                // Si se proporciona un nuevo cliente, actualizar la asociación
                if (dto.getCliente() != null) {
                        Cliente clienteAsociado = clienteRepository.findById(dto.getCliente())
                                        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
                        contenedorExistente.setCliente(clienteAsociado);
                }

                Contenedor contenedorActualizado = contenedorRepository.save(contenedorExistente);

                return toResponseDto(contenedorActualizado);
        }

        public ContenedorResponseDto getById(Long id) {
                Contenedor contenedor = contenedorRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
                return toResponseDto(contenedor);

        }

        public ContenedorEstadoDto obtenerEstado(Long idContenedor) {
                Contenedor contenedor = contenedorRepository.findById(idContenedor)
                                .orElseThrow(() -> new RuntimeException(
                                                "Contenedor no encontrado con id: " + idContenedor));

                return mapToContenedorEstadoDto(contenedor);
        }

        // Obtener todos los contenedores que no estén en estado ENTREGADO
        // y permite filtrar por estado actual
        public List<ContenedorEstadoDto> obtenerPendientes(String estado) {
                List<Contenedor> contenedoresPendientes = contenedorRepository.findAll().stream()
                                .filter(c -> c.getEstado() != Contenedor.EstadoContenedor.ENTREGADO)
                                .collect(Collectors.toList());

                // Si se proporciona un estado, filtrar por ese estado
                if (estado != null && !estado.isEmpty()) {
                        contenedoresPendientes = contenedoresPendientes.stream()
                                        .filter(c -> c.getEstado().name().equalsIgnoreCase(estado))
                                        .collect(Collectors.toList());
                }

                return contenedoresPendientes.stream()
                                .map(this::mapToContenedorEstadoDto)
                                .collect(Collectors.toList());
        }

        // Mapeo auxiliar de Contenedor a ContenedorEstadoDto
        private ContenedorEstadoDto mapToContenedorEstadoDto(Contenedor contenedor) {
                Cliente cliente = contenedor.getCliente();

                // Obtener la solicitud actual del contenedor
                Solicitud solicitud = solicitudRepository.findByContenedorId(contenedor.getId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Solicitud no encontrada para el contenedor con id: "
                                                                + contenedor.getId()));

                // Obtener la ruta asociada a la solicitud
                Ruta ruta;
                if (solicitud.getIdRutaSeleccionada() != null) {
                        ruta = rutaRepository.findById(solicitud.getIdRutaSeleccionada())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Ruta no encontrada para la solicitud con id: "
                                                                        + solicitud.getId()));
                } else {
                        List<Ruta> rutas = rutaRepository.findAllBySolicitudId(solicitud.getId());
                        if (rutas == null || rutas.isEmpty()) {
                                log.error("La solicitud no tiene ruta seleccionada");
                                throw new RuntimeException(
                                                "Ruta no encontrada para la solicitud con id: " + solicitud.getId());
                        }
                        log.error("La solicitud no tiene ruta seleccionada");
                        throw new RuntimeException(
                                                "Ruta no encontrada para la solicitud con id: " + solicitud.getId());
                }

                // Obtener la ubicación actual
                String ubicacionActual = obtenerUbicacionContenedor(contenedor, ruta);

                return ContenedorEstadoDto.builder()
                                .idContenedor(contenedor.getId())
                                .estados(contenedor.getHistorialEstados())
                                .estadoActual(contenedor.getEstado().name())
                                .ubicacionActual(ubicacionActual)
                                .clienteNombre(cliente.getNombre())
                                .clienteDni(cliente.getDni())
                                .idSolicitud(solicitud.getId())
                                .build();
        }

        // Obtener la ubicacion de los contenedores en base a su estado
        public String obtenerUbicacionContenedor(Contenedor contenedor, Ruta ruta) {
                String ubicacionActual = "";
                switch (contenedor.getEstado()) {
                        case EN_VIAJE:

                                // Obtener el tramo actual del contenedor
                                ubicacionActual = ruta.getTramos().stream()
                                                .filter(t -> t.getFechaHoraInicio() != null
                                                                && t.getFechaHoraFin() == null)
                                                .findFirst()
                                                .map(t -> t.getId().toString())
                                                .orElse("Tramo no encontrado");
                                break;

                        case EN_DEPOSITO:

                                // A partir del primer tramo no iniciado, obtener el depósito
                                ubicacionActual = ruta.getTramos().stream()
                                                .filter(t -> t.getFechaHoraInicio() == null)
                                                .findFirst()
                                                .map(t -> {
                                                        Long idOrigen = t.getIdOrigen();
                                                        String tipoOrigen = t.getTipoOrigen();
                                                        if (tipoOrigen.equals("DEPOSITO")) {
                                                                return idOrigen.toString();
                                                        } else {
                                                                return "No en depósito";
                                                        }
                                                })
                                                .orElse("Depósito no encontrado");
                                break;

                        // Debe coincidir con direccionOrigen de la solicitud
                        case EN_ORIGEN:
                                ubicacionActual = solicitudRepository.findById(ruta.getSolicitud().getId())
                                                .map(solicitud -> solicitud.getDireccionOrigen())
                                                .orElse("Origen no encontrado");
                                break;
                        // Debe coincidir con direccionDestino de la solicitud
                        case ENTREGADO:
                                ubicacionActual = solicitudRepository.findById(ruta.getSolicitud().getId())
                                                .map(solicitud -> solicitud.getDireccionDestino())
                                                .orElse("Destino no encontrado");
                                break;
                }
                return ubicacionActual;
        }
}
