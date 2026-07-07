package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UbicacionRequestDto {
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idCiudad;
}
