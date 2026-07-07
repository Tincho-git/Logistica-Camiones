package ar.edu.utn.frc.back3k7.ServicioLogistica.models;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "DEPOSITOS")
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deposito")
    private Long id;

    private String direccion;
    private double latitud;
    private double longitud;

    // Cada depósito debe mantener un costo de estadía diario    
    @Column(name = "costo_estadia_diaria")
    private double costoEstadiaDiaria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ciudad", nullable = false)
    private Ciudad ciudad;

    @OneToMany(mappedBy = "deposito", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EstadiaDeposito> estadias;
}
