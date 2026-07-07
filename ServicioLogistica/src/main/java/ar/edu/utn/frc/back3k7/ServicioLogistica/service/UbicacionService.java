package ar.edu.utn.frc.back3k7.ServicioLogistica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Ciudad;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Ubicacion;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.UbicacionRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.UbicacionResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.CiudadRepository;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.UbicacionRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UbicacionService {

    @Autowired
    private final UbicacionRepository ubicacionRepository;

    @Autowired
    private final CiudadRepository ciudadRepository;

    // DTO -> Entity
    public Ubicacion toEntity(UbicacionRequestDto ubicacionRequestDto) {

        // Paso el idCiudad a Ciudad para agregar a la entidad Ubicacion
        Ciudad ciudad = ciudadRepository.findById(ubicacionRequestDto.getIdCiudad())
                .orElseThrow(() -> new RuntimeException(
                        "Ciudad no encontrada con id: "
                                + ubicacionRequestDto.getIdCiudad()));

        return Ubicacion.builder()
                .direccion(ubicacionRequestDto.getDireccion())
                .latitud(ubicacionRequestDto.getLatitud())
                .longitud(ubicacionRequestDto.getLongitud())
                .ciudad(ciudad)
                .build();
    }

    // Entity -> DTO
    public UbicacionResponseDto toDto(Ubicacion ubicacion) {
        return UbicacionResponseDto.builder()
                .id(ubicacion.getId())
                .direccion(ubicacion.getDireccion())
                .latitud(ubicacion.getLatitud())
                .longitud(ubicacion.getLongitud())
                .idCiudad(ubicacion.getCiudad().getId())
                .build();
    }

    // Crea una nueva Ubicacion en la BD
    public UbicacionResponseDto registrarUbicacion(UbicacionRequestDto ubicacionRequestDto) {
        Ubicacion ubicacion = toEntity(ubicacionRequestDto);

        Ubicacion ubicacionGuardada = ubicacionRepository.save(ubicacion);

        return toDto(ubicacionGuardada);
    }

    // Actualiza una Ubicacion ya existente
    public UbicacionResponseDto actualizarUbicaicon(Long id, UbicacionRequestDto ubicacionRequestDto) {
        Ubicacion ubicacionExistente = ubicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ubicacion no encontrada con id: " + id));

        ubicacionExistente.setDireccion(ubicacionRequestDto.getDireccion());
        ubicacionExistente.setLatitud(ubicacionRequestDto.getLatitud());
        ubicacionExistente.setLongitud(ubicacionRequestDto.getLongitud());

        Ciudad ciudad = ciudadRepository.findById(ubicacionRequestDto.getIdCiudad())
                .orElseThrow(() -> new RuntimeException(
                        "Ciudad no encontrada con id: "
                                + ubicacionRequestDto.getIdCiudad()));
        ubicacionExistente.setCiudad(ciudad);

        Ubicacion UbicacionActualizada = ubicacionRepository.save(ubicacionExistente);

        return toDto(UbicacionActualizada);
    }

    public UbicacionResponseDto buscarUbicacionPorId(Long id) {
        Ubicacion ubicacion = ubicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ubicacion no encontrada con id: " + id));
        return toDto(ubicacion);
    }

    // Buscar Ubicacion por coordenadas (latitud y longitud), si no la encuentra la crea
    public UbicacionResponseDto buscarUbicacionPorCoordenadas(double latitud, double longitud, String nombreCiudad, String direccion) {


        Ubicacion ubicacion = ubicacionRepository.buscarPorCoordenadasAprox(latitud, longitud)
                .orElseGet(() -> {
                    Ciudad ciudad = ciudadRepository.findByNombre(nombreCiudad)
                            .orElseGet(() -> {
                                Ciudad nuevaCiudad  = Ciudad.builder()
                                        .nombre(nombreCiudad)
                                        .build();
                                return ciudadRepository.save(nuevaCiudad);

                    });


                    // Si no se encuentra, crear una nueva Ubicacion
                    Ubicacion nuevaUbicacion = Ubicacion.builder()
                            .direccion(direccion)
                            .latitud(latitud)
                            .longitud(longitud)
                            .ciudad(ciudad)
                            .build();
                    return ubicacionRepository.save(nuevaUbicacion);
                });
        return toDto(ubicacion);
    }

    public List<UbicacionResponseDto> buscarTodas() {
        return ubicacionRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }
}
