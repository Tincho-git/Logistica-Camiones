package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);
}
