package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto;

import java.util.List;

import lombok.Data;

@Data
public class DepositoClientDto {
    private Long id;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idCiudad;
    private List<Long> idEstadias;
    private double costoEstadiaDiaria;  
}
