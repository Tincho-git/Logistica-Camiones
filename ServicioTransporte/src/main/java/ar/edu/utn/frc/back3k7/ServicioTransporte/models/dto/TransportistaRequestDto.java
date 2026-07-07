package ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportistaRequestDto {
    private String dni;
    private char tipoDni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private Long idCamion;
}