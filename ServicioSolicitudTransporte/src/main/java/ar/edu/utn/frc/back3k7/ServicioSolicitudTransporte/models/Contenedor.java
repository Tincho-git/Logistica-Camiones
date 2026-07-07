package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="CONTENEDORES")
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contenedor")
    private Long id;

    private double ancho;
    private double largo;
    private double peso;
    private double altura;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private EstadoContenedor estado = EstadoContenedor.EN_ORIGEN;

    @Builder.Default
    private List<String> historialEstados = new ArrayList<>(List.of(EstadoContenedor.EN_ORIGEN.name()));

    @ManyToOne
    @JoinColumn(name = "id_cliente",referencedColumnName = "id_cliente")
    private Cliente cliente;


    public enum EstadoContenedor {
        EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO
    }

    public double getVolumen() {
        return ancho * largo * altura;
    }
}

