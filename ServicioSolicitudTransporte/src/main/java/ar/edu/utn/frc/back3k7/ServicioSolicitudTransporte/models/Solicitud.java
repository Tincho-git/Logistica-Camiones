package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "SOLICITUDES")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Column(name = "costo_final")
    private double costoFinal;

    @Column(name = "tiempo_real")
    private double tiempoReal;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Column(name = "costo_estimado")
    private double costoEstimado;

    @Column(name = "tiempo_estimado")
    private double tiempoEstimado;

    private String direccionOrigen;
    private String direccionDestino;

    /**
     * Las solicitudes deben registrar un estado, por ejemplo: [borrador - programada - en tránsito
     * entregada - cancelada]
     */

    @Builder.Default
    @Column(name = "estado_solicitud")
    private EstadoSolicitud estado = EstadoSolicitud.BORRADOR;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_contenedor",referencedColumnName = "id_contenedor")
    private Contenedor contenedor;

    @Column(name = "id_tarifa")
    private Long idTarifa;

    @ElementCollection
    @Builder.Default
    private List<Long> idEstadiasDepositos = new ArrayList<>();

    @Column(name = "id_ruta_seleccionada")
    private Long idRutaSeleccionada;


    public enum EstadoSolicitud {
        BORRADOR, PROGRAMADA, EN_TRANSITO , ENTREGADA
    }

    
}
