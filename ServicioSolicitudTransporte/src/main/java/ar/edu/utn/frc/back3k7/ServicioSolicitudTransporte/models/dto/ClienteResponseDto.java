package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClienteResponseDto {

    private Long id;
    private String dni;
    private char tipoDni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}
