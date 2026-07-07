package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByIdEstadiasDepositosIn(List<Long> idEstadias);

    // Busca una solicitud por el ID del contenedor asociado
    Optional<Solicitud> findByContenedorId(Long idContenedor);
}
