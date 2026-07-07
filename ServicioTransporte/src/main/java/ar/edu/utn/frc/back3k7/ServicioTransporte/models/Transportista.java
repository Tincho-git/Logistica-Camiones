package ar.edu.utn.frc.back3k7.ServicioTransporte.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="TRANSPORTISTAS")
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_transportista")
    private Long id;

    private String dni;

    @Column(name="tipo_dni")
    private char tipoDni;
    @Column(name="nombre_transportista")
    private String nombre;
    @Column(name="apellido_transportista")
    private String apellido;
    @Column(name="telefono_transportista")
    private String telefono;
    @Column(name="email_transportista")
    private String email;

    // FetchType.EAGER porque la relacion es 1 a 1
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_camion",referencedColumnName = "id_camion")
    private Camion Camion;

}
