package ar.edu.utn.frc.back3k7.ServicioLogistica.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.Ubicacion;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    @Query("SELECT u FROM Ubicacion u WHERE ABS(u.latitud - :lat) < 0.00001 AND ABS(u.longitud - :lon) < 0.00001")
    Optional<Ubicacion> buscarPorCoordenadasAprox(
            @Param("lat") double latitud,
            @Param("lon") double longitud);

}
