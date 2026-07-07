package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
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
@Table(name="RUTAS")
public class Ruta {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long id;

    @Column(name = "cantidad_tramos")
    private int cantidadTramos;

    @Column(name = "cantidad_depositos")
    private int cantidadDepositos;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_solicitud",referencedColumnName = "id_solicitud")
    private Solicitud solicitud;

    @OneToMany(mappedBy = "ruta" , fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn(name = "tramo_order")
    private List<Tramo> tramos;

    @Column(name="distancia_total_km")
    private double distanciaTotalKm;

    @Column(name="tiempo_estimado")
    private double tiempoEstimado;

    @Column(name = "costo_estimado")
    private double costoEstimado;
}
