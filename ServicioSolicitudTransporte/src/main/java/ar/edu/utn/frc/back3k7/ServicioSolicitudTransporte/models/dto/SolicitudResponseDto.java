package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import java.time.LocalDateTime;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.TarifaClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Solicitud.EstadoSolicitud;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class SolicitudResponseDto {


    /***
     *  GET /solicitudes/1
     *
     * {
     *   "idSolicitud": 1,
     *   "estado": "BORRADOR",
     *   "idContenedor": 1,
     *   "estadoContenedor": "NO_RETIRADO",
     *   "ancho": 2.5,
     *   "largo": 6.0,
     *   "peso": 1200,
     *   "altura": 2.8,
     *   "clienteNombre": "Juan",
     *   "clienteApellido": "Pérez",
     *   "clienteDni": "12345678",
     *   "clienteTelefono": "3511234567",
     *   "clienteEmail": "juan@mail.com",
     *   "costoEstimado": 0.0,
     *   "costoFinal": 0.0,
     *   "tiempoEstimado": null,
     *   "tiempoReal": null,
     *   "cantidadTramos": 0
     * }
     *
     *
     */
    private Long idSolicitud;
    private double costoFinal;
    private double tiempoReal;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private double costoEstimado;
    private double tiempoEstimado;
    private EstadoSolicitud estado;
    private Long idContenedor;
    private String clienteDni;
    private String direccionOrigen;
    private String direccionDestino;

    private TarifaClientDto tarifa;
}
