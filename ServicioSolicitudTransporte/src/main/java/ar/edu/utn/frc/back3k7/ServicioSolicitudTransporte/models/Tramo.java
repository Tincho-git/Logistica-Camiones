package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TRAMOS")
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_ruta", referencedColumnName = "id_ruta")
    private Ruta ruta;

    @Column(name = "tipo_tramo")
    private TipoTramo tipoTramo;

    @Column(name = "distancia_km")
    private double distanciaKm;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    private Long idOrigen;
    private String tipoOrigen;   // "DEPOSITO" u "ORIGEN"

    private Long idDestino;
    private String tipoDestino;  // "DEPOSITO" o "DESTINO"

    @Column(name = "id_camion")
    private Long idCamion;
 
    public enum TipoTramo {
        ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, DEPOSITO_DESTINO, ORIGEN_DESTINO
    }

    

}
