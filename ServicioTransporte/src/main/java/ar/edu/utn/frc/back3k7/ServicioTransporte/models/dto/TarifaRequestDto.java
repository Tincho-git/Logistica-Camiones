package ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TarifaRequestDto {
    @NotNull
    private Double rangoPesoMin;
    @NotNull
    private Double rangoPesoMax;
    @NotNull
    private Double rangoVolumenMin;
    @NotNull
    private Double rangoVolumenMax;
    @NotNull
    private Double costoCombustibleLitro;
}
