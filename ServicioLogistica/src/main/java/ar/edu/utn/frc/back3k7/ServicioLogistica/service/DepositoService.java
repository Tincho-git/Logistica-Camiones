package ar.edu.utn.frc.back3k7.ServicioLogistica.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Ciudad;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Deposito;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.EstadiaDeposito;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.DepositoRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.DepositoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.CiudadRepository;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.DepositoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DepositoService {

        @Autowired
        private final DepositoRepository depositoRepository;

        @Autowired
        private final CiudadRepository ciudadRepository;

        // DTO -> Entity
        public Deposito toEntity(DepositoRequestDto depositoRequestDto) {

                // Paso el idCiudad a Ciudad para agregar a la entidad Deposito
                Ciudad ciudad = ciudadRepository.findById(depositoRequestDto.getIdCiudad())
                                .orElseThrow(() -> new RuntimeException(
                                                "Ciudad no encontrada con id: "
                                                                + depositoRequestDto.getIdCiudad()));

                return Deposito.builder()
                                .direccion(depositoRequestDto.getDireccion())
                                .latitud(depositoRequestDto.getLatitud())
                                .longitud(depositoRequestDto.getLongitud())
                                .costoEstadiaDiaria(depositoRequestDto.getCostoEstadiaDiaria())
                                .ciudad(ciudad)
                                .build();
        }

        // Entity -> DTO
        public DepositoResponseDto toDto(Deposito deposito) {

                // Paso List<EstadiaDeposito> a List<idEstadias> para agregar al DTO Deposito
                List<EstadiaDeposito> estadias = deposito.getEstadias();
                List<Long> idEstadias = new ArrayList<>();

                if (estadias != null) {
                        for (EstadiaDeposito estadia : estadias) {
                                idEstadias.add(estadia.getId());
                        }
                }

                return DepositoResponseDto.builder()
                                .id(deposito.getId())
                                .direccion(deposito.getDireccion())
                                .latitud(deposito.getLatitud())
                                .longitud(deposito.getLongitud())
                                .costoEstadiaDiaria(deposito.getCostoEstadiaDiaria())
                                .idCiudad(deposito.getCiudad().getId())
                                .idEstadias(idEstadias)
                                .build();
        }

        // Crea un nuevo Deposito en la BD
        public DepositoResponseDto registrarDeposito(DepositoRequestDto depositoRequestDto) {
                Deposito deposito = toEntity(depositoRequestDto);

                Deposito depositoGuardado = depositoRepository.save(deposito);

                return toDto(depositoGuardado);
        }

        // Actualiza un Deposito ya existente
        public DepositoResponseDto actualizarDeposito(Long id, DepositoRequestDto depositoRequestDto) {
                Deposito depositoExistente = depositoRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Depósito {} no encontrado", id);
                                        return new RuntimeException("Depósito no encontrado");
                                });

                depositoExistente.setDireccion(depositoRequestDto.getDireccion());
                depositoExistente.setLatitud(depositoRequestDto.getLatitud());
                depositoExistente.setLongitud(depositoRequestDto.getLongitud());
                depositoExistente.setCostoEstadiaDiaria(depositoRequestDto.getCostoEstadiaDiaria());

                Ciudad ciudad = ciudadRepository.findById(depositoRequestDto.getIdCiudad())
                                .orElseThrow(() -> {
                                        log.error("Ciudad {} no encontrada", depositoRequestDto.getIdCiudad());
                                        return new RuntimeException("Ciudad no encontrada");
                                });
                depositoExistente.setCiudad(ciudad);

                Deposito depositoActualizado = depositoRepository.save(depositoExistente);

                return toDto(depositoActualizado);
        }

        public DepositoResponseDto buscarDepositoPorId(Long id) {
                Deposito deposito = depositoRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Depósito {} no encontrado", id);
                                        return new RuntimeException("Depósito no encontrado");
                                });
                return toDto(deposito);
        }

        // Buscar Deposito por coordenadas (latitud y longitud)
        public DepositoResponseDto buscarDepositoPorCoordenadas(double latitud, double longitud) {
                Deposito deposito = depositoRepository.buscarPorCoordenadasAprox(latitud, longitud)
                                .orElseThrow(() -> {
                                        log.error("Depósito con lat={} y lon{} no encontrado", latitud, longitud);
                                        return new RuntimeException("Depósito no encontrado");
                                });
                return toDto(deposito);
        }

        public List<DepositoResponseDto> buscarTodos() {
                return depositoRepository.findAll()
                                .stream()
                                .map(this::toDto)
                                .toList();
        }
}
