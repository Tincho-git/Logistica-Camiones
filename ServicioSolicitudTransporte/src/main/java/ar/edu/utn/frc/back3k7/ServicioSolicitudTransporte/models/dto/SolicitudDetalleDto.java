package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudDetalleDto {
    private Long idSolicitud;
    private String estado;

    private Long idContenedor;
    private String estadoContenedor;
    private double ancho;
    private double largo;
    private double peso;
    private double altura;

    private String clienteNombre;
    private String clienteApellido;
    private String clienteDni;
    private String clienteTelefono;
    private String clienteEmail;

    private double costoEstimado;
    private double costoFinal;
    private double tiempoEstimado;
    private double tiempoReal;

    private int cantidadTramos;
    private Long idTarifa;

    private List<Long> idEstadiasDepositos;
}

