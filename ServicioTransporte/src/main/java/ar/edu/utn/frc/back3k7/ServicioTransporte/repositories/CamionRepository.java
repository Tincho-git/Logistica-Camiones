package ar.edu.utn.frc.back3k7.ServicioTransporte.repositories;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends JpaRepository<Camion,Long> {
}
