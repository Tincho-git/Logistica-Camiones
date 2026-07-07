package ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositoRequestDto {
    private String direccion;
    private Double latitud;
    private Double longitud;
    private double costoEstadiaDiaria;
    private Long idCiudad;
}
