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
public class ContenedorEstadoDto {


    /***
     *
     * GET /contenedores/1/estado
     *
     * {
     *   "idContenedor": 1,
     *   "estados": ["EN_DESTINO", "EN_VIAJE", "EN_DEPOSITO"],
     *   "estadoActual": "EN_DEPOSITO",
     *   "ubicacionActual": "{idDeposito}",
     *   "clienteNombre": "Juan",
     *   "clienteDni": "12345678",
     *   "idSolicitud": 1
     * }
     * 
     * {
     *   "idContenedor": 1,
     *   "estados": ["EN_DESTINO", "EN_VIAJE", "EN_DEPOSITO", "EN_VIAJE"],
     *   "estadoActual": "EN_VIAJE",
     *   "ubicacionActual": "{idTramo}",
     *   "clienteNombre": "Juan",
     *   "clienteDni": "12345678",
     *   "idSolicitud": 1
     * }
     *
     */
    private Long idContenedor;
    private List<String> estados;
    private String estadoActual;
    private String ubicacionActual;
    private String clienteNombre;
    private String clienteDni;
    private Long idSolicitud;
}
