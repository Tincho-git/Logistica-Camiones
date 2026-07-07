package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CoordenadaDto {


    /**
     *
     * EJEMPLOS:
     * {
     *   "origen": { "lat": -31.4135, "lon": -64.18105, "tipo": "ORIGEN" }
     * }
     *
     * "destino": { "lat": -31.6333, "lon": -60.7000, "tipo": "DESTINO"}
     */
    private double lat;
    private double lon;
    // ORIGEN, DESTINO, DEPOSITO
    private String tipo;
}
