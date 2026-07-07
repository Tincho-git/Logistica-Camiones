package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RutaTentativaDto {

    /** RESPONSE DE calcularRutaTentativa()
     *
     *
     *
     * {
     *   "tramos": [
     *     {
     *       "tipoTramo": "ORIGEN_DESTINO",
     *       "distanciaKm": 350.12,
     *       "tiempoHoras": 4.1,
     *     },
     *   ],
     *   "distanciaTotalKm": 350.12,
     *   "tiempoTotalHoras": 4.1,
     * }
     */
    private Long idRuta;
    private int cantidadTramos;
    private int cantidadDepositos;
    private double distanciaTotalKm;
    private double costoEstimado;
    private double tiempoEstimado;
    private List<TramoDto> tramos;
}
