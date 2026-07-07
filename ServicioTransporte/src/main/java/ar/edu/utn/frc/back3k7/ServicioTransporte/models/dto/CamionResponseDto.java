package ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CamionResponseDto {
    private Long id;
    private String patente;
    private double volumen;
    private double peso;
    private double costoBaseKm;
    private boolean disponibilidad;
    private double consumoCombusProm;
    private Long idTransportista;
    private Long idTarifa;
}
