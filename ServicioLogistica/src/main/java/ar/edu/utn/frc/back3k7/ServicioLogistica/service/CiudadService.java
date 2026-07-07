package ar.edu.utn.frc.back3k7.ServicioLogistica.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Ciudad;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Deposito;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Ubicacion;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.CiudadRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.CiudadResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.CiudadRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CiudadService {

        @Autowired
        private final CiudadRepository ciudadRepository;

        // DTO -> Entity
        public Ciudad toEntity(CiudadRequestDto ciudadRequestDto) {
                return Ciudad.builder()
                                .nombre(ciudadRequestDto.getNombre())
                                .build();
        }

        // Entity -> DTO
        public CiudadResponseDto toDto(Ciudad ciudad) {
                
                // Paso List<Deposito> a List<idDepositos> para agregar al DTO Ciudad
                List<Deposito> depositos = ciudad.getDepositos();
                List<Long> idDepositos = new ArrayList<>();

                if (depositos != null) {
                        for (Deposito deposito : depositos) {
                                idDepositos.add(deposito.getId());
                        }
                }

                // Paso List<Ubicacion> a List<idUbicaciones> para agregar al DTO Ciudad
                List<Ubicacion> ubicaciones = ciudad.getUbicaciones();
                List<Long> idUbicaciones = new ArrayList<>();

                if (ubicaciones != null) {
                        for (Ubicacion ubicacion : ubicaciones) {
                                idUbicaciones.add(ubicacion.getId());
                        }
                }

                return CiudadResponseDto.builder()
                                .id(ciudad.getId())
                                .nombre(ciudad.getNombre())
                                .idDepositos(idDepositos)
                                .idUbicaciones(idUbicaciones)
                                .build();
        }

        // Crea un nueva ciudad en la BD
        public CiudadResponseDto registrarCiudad(CiudadRequestDto ciudadRequestDto) {
                Ciudad ciudad = toEntity(ciudadRequestDto);

                Ciudad ciudadGuardada = ciudadRepository.save(ciudad);

                return toDto(ciudadGuardada);
        }

        // Actualiza una ciudad ya existente
        public CiudadResponseDto actualizarCiudad(Long id, CiudadRequestDto ciudadRequestDto) {
                Ciudad ciudadExistente = ciudadRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con id: " + id));

                ciudadExistente.setNombre(ciudadRequestDto.getNombre());

                Ciudad ciudadActualizada = ciudadRepository.save(ciudadExistente);

                return toDto(ciudadActualizada);
        }

        public CiudadResponseDto buscarCiudadPorId(Long id) {
                Ciudad ciudad = ciudadRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada con id: " + id));
                return toDto(ciudad);
        }

        public List<CiudadResponseDto> buscarTodas(){
                return ciudadRepository.findAll()
                        .stream()
                        .map(this::toDto)
                        .toList();
        }
}
