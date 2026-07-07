package ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CamionRequestDto {
    private String patente;
    private double volumen;
    private double peso;
    private double costoBaseKm;
    private double consumoCombusProm;
    private Long idTransportista;
}
