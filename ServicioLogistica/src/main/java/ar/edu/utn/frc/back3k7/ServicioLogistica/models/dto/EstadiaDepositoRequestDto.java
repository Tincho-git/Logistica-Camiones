package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadiaDepositoRequestDto {
    private Long idDeposito;
    private String fechaHoraEntrada;
    private String fechaHoraSalida;
    private Long idSolicitud;
}
