package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CiudadResponseDto {
    private Long id;
    private String nombre;
    private List<Long> idDepositos;
    private List<Long> idUbicaciones;
}
