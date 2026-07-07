package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TramoDto {


    /**
     *
     *      {
     *       "tipoTramo": "DEPOSITO_DESTINO",
     *       "distanciaKm": 300.0,
     *       "tiempoHoras": 4.0,
     *     }
     *
     *    {
     *       "tipoTramo": "ORIGEN_DESTINO",
     *       "distanciaKm": 350.12,
     *       "tiempoHoras": 4.1,
     *     }
     */

    private String tipoTramo;    // ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, DEPOSITO_DESTINO, ORIGEN_DESTINO
    private double distanciaKm;  // desde OSRM (metros -> km)
    private double tiempoHoras;  // desde OSRM (segundos -> horas)
    private Long idCamionAsignado;

    private Long idOrigen;
    private String tipoOrigen;   // "DEPOSITO" o "UBICACION"

    private Long idDestino;
    private String tipoDestino;  // "DEPOSITO" o "UBICACION"


    public enum TipoTramo {
        ORIGEN_DESTINO,
        ORIGEN_DEPOSITO,
        DEPOSITO_DEPOSITO,
        DEPOSITO_DESTINO
    }
}
