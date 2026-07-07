package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
    List<Tramo> findByIdCamion(Long idCamion);
}
