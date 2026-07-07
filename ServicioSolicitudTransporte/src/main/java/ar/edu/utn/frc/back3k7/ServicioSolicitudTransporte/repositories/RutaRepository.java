package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Ruta;


@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    Optional<Ruta> findBySolicitudId(Long solicitudId);
    java.util.List<Ruta> findAllBySolicitudId(Long solicitudId);
}
