package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepositoResponseDto {
    private Long id;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private double costoEstadiaDiaria;
    private Long idCiudad;
    private List<Long> idEstadias;
}
