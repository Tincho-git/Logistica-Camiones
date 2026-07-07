package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models;

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
@Table(name = "CLIENTES")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    private String dni;

    @Column(name = "tipo_dni")
    private char tipoDni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;


}
