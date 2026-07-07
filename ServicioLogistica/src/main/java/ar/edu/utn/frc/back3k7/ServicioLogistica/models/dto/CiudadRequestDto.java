package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CiudadRequestDto {
    private String nombre;
}
