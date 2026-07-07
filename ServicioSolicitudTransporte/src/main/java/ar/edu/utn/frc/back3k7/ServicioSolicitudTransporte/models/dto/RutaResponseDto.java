package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RutaResponseDto {
    private Long idRuta;
    private Long idSolicitud;
    private int cantidadTramos;
    private int cantidadDepositos;
    private double distanciaTotalKm;
    private double costoEstimado;
    private double tiempoEstimado;
    private List<TramoResponseDto> tramos;
}
