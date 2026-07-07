package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContenedorResponseDto {

    private Long id;
    private double ancho;
    private double largo;
    private double peso;
    private double altura;
    private String cliente;
}
