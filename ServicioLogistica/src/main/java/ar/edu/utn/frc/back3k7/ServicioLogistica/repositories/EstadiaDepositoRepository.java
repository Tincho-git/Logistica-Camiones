package ar.edu.utn.frc.back3k7.ServicioLogistica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.EstadiaDeposito;

import java.util.List;

@Repository
public interface EstadiaDepositoRepository extends JpaRepository<EstadiaDeposito, Long> {
    List<EstadiaDeposito> findByDepositoIdAndFechaHoraSalidaIsNull(Long depositoId);
}
