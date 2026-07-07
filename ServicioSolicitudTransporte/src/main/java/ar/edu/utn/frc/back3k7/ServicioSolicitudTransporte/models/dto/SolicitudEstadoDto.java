package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudEstadoDto {


    /**
     *
     * GET /solicitudes/1/estado
     * {
     *   "idSolicitud": 1,
     *   "estado": "BORRADOR"
     * }
     *
     */
    private Long idSolicitud;
    private String estado;
}
