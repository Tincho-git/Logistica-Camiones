package ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TarifaResponseDto {
    private Long id;
    private Double rangoPesoMin;
    private Double rangoPesoMax;
    private Double rangoVolumenMin;
    private Double rangoVolumenMax;
    private Double costoCombustibleLitro;
    private Double consumoCombustibleGralAprox;
    private Double costoBaseKm;
    private List<Long> idCamiones;
}
