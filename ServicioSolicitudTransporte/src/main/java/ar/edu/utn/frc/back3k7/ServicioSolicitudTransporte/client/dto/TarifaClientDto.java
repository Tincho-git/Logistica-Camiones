package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto;

import java.util.List;

import lombok.Data;

@Data
public class TarifaClientDto {
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
