package ar.edu.utn.frc.back3k7.ServicioTransporte.repositories;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
}

