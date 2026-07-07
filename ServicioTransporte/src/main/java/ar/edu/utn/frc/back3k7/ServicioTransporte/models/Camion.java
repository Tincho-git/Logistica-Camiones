package ar.edu.utn.frc.back3k7.ServicioTransporte.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="CAMIONES")
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_camion")
    private Long id;

    private String patente;

    private double volumen;
    private double peso;

    // Los camiones deben conocer su costo base de traslado por km
    // para el calculo del costo real
    @Column(name="costo_base_km")
    private double costoBaseKm;
    
    // @Builder.Default hace que el valor por defecto sea true al crear una instancia con el builder
    @Builder.Default
    private boolean disponibilidad = true;

    // Los camiones deben conocer su consumo de combustible promedio
    // para el calculo del costo real
    @Column(name="consumo_combustible_promedio")
    private double consumoCombusProm;

    // FetchType.EAGER porque la relacion es 1 a 1
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_transportista",referencedColumnName = "id_transportista")
    private Transportista transportista;

    @ManyToOne
    @JoinColumn(name = "id_tarifa", referencedColumnName = "id_tarifa")
    private Tarifa tarifa;

    //relacion con tramos

}
