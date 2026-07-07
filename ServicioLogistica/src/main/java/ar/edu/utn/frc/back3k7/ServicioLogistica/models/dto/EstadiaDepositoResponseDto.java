package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EstadiaDepositoResponseDto {
    private Long id;
    private Long idDeposito;
    private String fechaHoraEntrada;
    private String fechaHoraSalida;
    private Long idSolicitud;
}
