package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContenedorRequestDto {

    private double ancho;
    private double largo;
    private double peso;
    private double altura;
    private Long cliente;
}
