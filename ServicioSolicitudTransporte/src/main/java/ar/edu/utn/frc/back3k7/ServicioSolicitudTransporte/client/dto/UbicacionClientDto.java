package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto;

import lombok.Data;

@Data
public class UbicacionClientDto {
    private Long id;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Long idCiudad;  
}
