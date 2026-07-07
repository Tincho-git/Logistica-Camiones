package ar.edu.utn.frc.back3k7.ServicioLogistica.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Deposito;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Long> {
    
    @Query("SELECT d FROM Deposito d WHERE ABS(d.latitud - :lat) < 0.00001 AND ABS(d.longitud - :lon) < 0.00001")
    Optional<Deposito> buscarPorCoordenadasAprox(
            @Param("lat") double latitud,
            @Param("lon") double longitud);
}
