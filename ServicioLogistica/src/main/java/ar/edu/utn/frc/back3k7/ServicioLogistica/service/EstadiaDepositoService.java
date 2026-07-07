package ar.edu.utn.frc.back3k7.ServicioLogistica.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Deposito;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.EstadiaDeposito;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.EstadiaDepositoRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.EstadiaDepositoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.DepositoRepository;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.EstadiaDepositoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EstadiaDepositoService {

    @Autowired
    private EstadiaDepositoRepository estadiaDepositoRepository;

    @Autowired
    private DepositoRepository depositoRepository;

    public EstadiaDeposito toEntity(EstadiaDepositoRequestDto dto) {

        Deposito deposito = depositoRepository.findById(dto.getIdDeposito())
                .orElseThrow(() -> {
                    log.error("Depósito {} no encontrado", dto.getIdDeposito());
                    return new RuntimeException("Depósito no encontrado");
                });

        // Validar que la fecha de salida sea posterior a la de entrada si no es null
        if (dto.getFechaHoraSalida() != null) {
            LocalDateTime fechaHoraEntrada = LocalDateTime.parse(dto.getFechaHoraEntrada());
            LocalDateTime fechaHoraSalida = LocalDateTime.parse(dto.getFechaHoraSalida());
            if (fechaHoraSalida.isBefore(fechaHoraEntrada)) {
                log.error("La fecha y hora de salida debe ser posterior a la de entrada.");
                throw new RuntimeException();
            }
        }

        return EstadiaDeposito.builder()
                .deposito(deposito)
                .fechaHoraEntrada(LocalDateTime.parse(dto.getFechaHoraEntrada()))
                .fechaHoraSalida(
                        dto.getFechaHoraSalida() != null ? LocalDateTime.parse(dto.getFechaHoraSalida()) : null)
                .idSolicitud(dto.getIdSolicitud())
                .build();
    }

    public EstadiaDepositoResponseDto toDto(EstadiaDeposito estadia) {
        return EstadiaDepositoResponseDto.builder()
                .id(estadia.getId())
                .idDeposito(estadia.getDeposito().getId())
                .fechaHoraEntrada(estadia.getFechaHoraEntrada().toString())
                .fechaHoraSalida(estadia.getFechaHoraSalida() != null ? estadia.getFechaHoraSalida().toString() : null)
                .idSolicitud(estadia.getIdSolicitud())
                .build();
    }

    // Crea una nueva Estadía en la BD
    public EstadiaDepositoResponseDto registrarEstadia(EstadiaDepositoRequestDto dto) {
        EstadiaDeposito estadia = toEntity(dto);
        EstadiaDeposito estadiaGuardada = estadiaDepositoRepository.save(estadia);
        return toDto(estadiaGuardada);
    }

    // Actualiza la fecha y hora de salida de una estadía existente
    public EstadiaDepositoResponseDto actualizarFechaHoraSalidaEstadia(Long id, EstadiaDepositoRequestDto dto) {
        EstadiaDeposito estadiaExistente = estadiaDepositoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Estadía {} no encontrada", id);
                    return new RuntimeException("Estadía no encontrada");
                });

        // Validar que la fecha de salida sea posterior a la de entrada
        LocalDateTime fechaHoraEntrada = estadiaExistente.getFechaHoraEntrada();
        LocalDateTime fechaHoraSalida = LocalDateTime.parse(dto.getFechaHoraSalida());
        if (fechaHoraSalida.isBefore(fechaHoraEntrada)) {
            log.error("La fechaHoraEntrada {} es posterior a la fechaHoraSalida {}", fechaHoraEntrada, fechaHoraSalida);
            throw new RuntimeException("La fecha y hora de salida debe ser posterior a la de entrada.");
        }

        estadiaExistente.setFechaHoraSalida(fechaHoraSalida);
        EstadiaDeposito estadiaActualizada = estadiaDepositoRepository.save(estadiaExistente);
        return toDto(estadiaActualizada);
    }

    public EstadiaDepositoResponseDto buscarEstadiaDepositoPorId(Long id) {
        EstadiaDeposito estadia = estadiaDepositoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Estadia {} no encontrada", id);
                    return new RuntimeException("Estadia no encontrada");
                });
        return toDto(estadia);
    }

    /**
     * Ejemplos:
     * 
     * Entrada: 2025-11-10 23:55
     * Salida: 2025-11-11 00:10
     * → 1 día
     * 
     * Entrada: 2025-11-10 10:00
     * Salida: 2025-11-13 09:00
     * → 3 días
     * 
     */
    public long calcularDiasEstadia(Long idEstadiaDeposito) {
        EstadiaDeposito estadiaDeposito = estadiaDepositoRepository.findById(idEstadiaDeposito)
                .orElseThrow(() -> {
                    log.error("Estadia {} no encontrada", idEstadiaDeposito);
                    return new RuntimeException("Estadia no encontrada");
                });

        LocalDateTime fechaHoraEntrada = estadiaDeposito.getFechaHoraEntrada();
        LocalDateTime fechaHoraSalida = estadiaDeposito.getFechaHoraSalida();

        if (fechaHoraEntrada == null || fechaHoraSalida == null) {
            return 0;
        }

        long dias = ChronoUnit.DAYS.between(fechaHoraEntrada.toLocalDate(), fechaHoraSalida.toLocalDate());

        return dias < 0 ? 0 : dias;
    }
}
