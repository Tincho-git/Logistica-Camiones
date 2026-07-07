package ar.edu.utn.frc.back3k7.ServicioTransporte.models;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "TARIFAS")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Long id;

    @NotNull
    @Column(name = "rango_peso_min", nullable = false)
    private Double rangoPesoMin;

    @NotNull
    @Column(name = "rango_peso_max", nullable = false)
    private Double rangoPesoMax;

    @NotNull
    @Column(name = "rango_volumen_min", nullable = false)
    private Double rangoVolumenMin;

    @NotNull
    @Column(name = "rango_volumen_max", nullable = false)
    private Double rangoVolumenMax;

    /**
     * Debe existir un valor de litro de combustible configurado 
     * y el consumo de combustible aproximado general, 
     * que surge del promedio de los consumos de los camiones aptos,
     * para el cálculo aproximado (costo estimado)
     */

    // Valor de litro de combustible configurado
    @NotNull
    @Column(name = "costo_combustible_litro", nullable = false)
    private Double costoCombustibleLitro;

    // Valor de consumo de combustible aproximado general
    @Column(name = "consumo_combustible_gral_aprox")
    private double consumoCombustibleGralAprox;

    /**
     * Debe existir un valor de costo por kilómetro base
     * para el cálculo aproximado (costo estimado)
     * que depende del volumen del contenedor.
     */
    @Column(name = "costo_base_km")
    private Double costoBaseKm;

    @Builder.Default
    @OneToMany(mappedBy = "tarifa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Camion> camiones = new ArrayList<>();

    // relacion con solicitudes

    // Calcular consumoCombustibleGralAprox
    public void calcularConsumoCombustibleGralAprox() {

        if (this.camiones == null || this.camiones.isEmpty()) {
            this.consumoCombustibleGralAprox = 0.0;
            return;
        }

        double sumaConsumo = 0.0;
        for (Camion camion : this.camiones) {
            sumaConsumo += camion.getConsumoCombusProm();
        }
        this.consumoCombustibleGralAprox = sumaConsumo / this.camiones.size();
    }

    // Calcular costoBaseKm
    public void calcularCostoBaseKm() {
        if (this.camiones == null || this.camiones.isEmpty()) {
            this.costoBaseKm = 0.0;
            return;
        }

        double sumaCosto = 0.0;
        for (Camion camion : this.camiones) {
            sumaCosto += camion.getCostoBaseKm();
        }
        this.costoBaseKm = sumaCosto / this.camiones.size();
    }
}
