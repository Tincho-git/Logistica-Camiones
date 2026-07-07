package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TramoResponseDto {
    private Long idTramo;
    private Long idRuta;
    private String tipoTramo;
    private double distanciaKm;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Long idOrigen;
    private String tipoOrigen;
    private Long idDestino;
    private String tipoDestino;
    private Long idCamion;
}
