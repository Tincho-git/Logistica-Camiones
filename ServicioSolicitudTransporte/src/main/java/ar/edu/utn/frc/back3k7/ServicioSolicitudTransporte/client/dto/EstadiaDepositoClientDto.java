package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadiaDepositoClientDto {
    private Long id;
    private Long idDeposito;
    private String fechaHoraEntrada;
    private String fechaHoraSalida;
    private Long idSolicitud;
}
