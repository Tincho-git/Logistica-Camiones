package ar.edu.utn.frc.back3k7.ServicioTransporte.repositories;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista,Long> {
}
