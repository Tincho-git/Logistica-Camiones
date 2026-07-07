package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UbicacionResponseDto {
    private Long id;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idCiudad;
}
