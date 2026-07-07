package ar.edu.utn.frc.back3k7.ServicioTransporte.service;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Camion;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Transportista;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.TransportistaRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.CamionRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TransportistaRequestDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TransportistaResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TransportistaService {

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private CamionRepository camionRepository;

    public TransportistaResponseDto registrarTransportista(TransportistaRequestDto dto) {

        // 1. Crear transportista (aún sin camión)
        Transportista transportista = toEntity(dto);

        // 2. Guardarlo primero para evitar TransientObjectException
        transportista = transportistaRepository.save(transportista);

        // 3. Si viene un camión, vincular
        if (dto.getIdCamion() != null) {

            Camion camion = camionRepository.findById(dto.getIdCamion())
                    .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

            camion.setTransportista(transportista);

            transportista.setCamion(camion);

            camionRepository.save(camion);
            transportistaRepository.save(transportista); // opcional si la relación es OneToOne owning side camion
        }

        return toDto(transportista);
    }

    public TransportistaResponseDto actualizarTransportista(Long id, TransportistaRequestDto transportistaRequestDto) {
        Transportista transportistaExistente = transportistaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transportista {} no encontrado", id);
                    return new RuntimeException("Transportista no encontrado");
                });

        transportistaExistente.setDni(transportistaRequestDto.getDni());
        transportistaExistente.setTipoDni(transportistaRequestDto.getTipoDni());
        transportistaExistente.setNombre(transportistaRequestDto.getNombre());
        transportistaExistente.setApellido(transportistaRequestDto.getApellido());
        transportistaExistente.setTelefono(transportistaRequestDto.getTelefono());
        transportistaExistente.setEmail(transportistaRequestDto.getEmail());

        if (transportistaRequestDto.getIdCamion() != null) {
            transportistaExistente.setCamion(camionRepository.findById(transportistaRequestDto.getIdCamion())
                    .orElseThrow(() -> {
                        log.error("Camión {} no encontrado", transportistaRequestDto.getIdCamion());
                        return new RuntimeException("Camión no encontrado");
                    }));
        }

        Transportista transportistaActualizado = transportistaRepository.save(transportistaExistente);
        return toDto(transportistaActualizado);
    }

    public List<TransportistaResponseDto> listarTransportistas() {
        return transportistaRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TransportistaResponseDto obtenerTransportista(Long id) {
        Transportista transportista = transportistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transportista no encontrado con id: " + id));
        return toDto(transportista);
    }

    public Transportista buscarTransportistaPorId(Long id) {
        return transportistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transportista no encontrado con id: " + id));
    }

    private Transportista toEntity(TransportistaRequestDto dto) {

        Camion camion = null;
        if (dto.getIdCamion() != null) {
            camion = camionRepository.findById(dto.getIdCamion())
                    .orElseThrow(() -> new RuntimeException("Camión no encontrado con id: " + dto.getIdCamion()));
        }

        return Transportista.builder()
                .dni(dto.getDni())
                .tipoDni(dto.getTipoDni())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .Camion(camion)
                .build();
    }

    private TransportistaResponseDto toDto(Transportista transportista) {
        return TransportistaResponseDto.builder()
                .id(transportista.getId())
                .dni(transportista.getDni())
                .tipoDni(transportista.getTipoDni())
                .nombre(transportista.getNombre())
                .apellido(transportista.getApellido())
                .telefono(transportista.getTelefono())
                .email(transportista.getEmail())
                .idCamion(transportista.getCamion() != null ? transportista.getCamion().getId() : null)
                .build();
    }

}
