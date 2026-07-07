package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class SolicitudRequestDto {
    /***
     *
     * POST /solicitudes
     * {
     *   "dni": "12345678",
     *   "tipoDni": "DNI",
     *   "nombre": "Juan",
     *   "apellido": "Pérez",
     *   "telefono": "3511234567",
     *   "email": "juan@mail.com",
     *   "ancho": 2.5,
     *   "largo": 6.0,
     *   "peso": 1200,
     *   "altura": 2.8,
     *   "origenDireccion": "Av. Siempre Viva 123",
     *   "destinoDireccion": "Puerto de Buenos Aires"
     * }
     *
     */
    private String dni;
    private String tipoDni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    private double ancho;
    private double largo;
    private double altura;
    private double peso;

    private double latOrigen;
    private double lonOrigen;
    private String tipoOrigen;
    private String direccionOrigen;
    private String ciudadOrigen;

    private double latDestino;
    private double lonDestino;
    private String tipoDestino;
    private String direccionDestino;
    private String ciudadDestino;

}
