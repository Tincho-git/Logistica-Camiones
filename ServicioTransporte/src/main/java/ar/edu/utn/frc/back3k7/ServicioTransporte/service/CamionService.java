package ar.edu.utn.frc.back3k7.ServicioTransporte.service;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.*;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.CamionRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.TarifaRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.TransportistaRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.CamionRequestDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.CamionResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CamionService {

    @Autowired
    private CamionRepository camionRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    // Crea un nuevo camion en la BD
    public CamionResponseDto registrarCamion(CamionRequestDto dto) {

        Camion nuevoCamion = toEntity(dto);

        // Buscar tarifa cuyo rango aplique
        Tarifa tarifa = tarifaRepository.findAll().stream()
                .filter(t -> nuevoCamion.getPeso() >= t.getRangoPesoMin()
                && nuevoCamion.getPeso() <= t.getRangoPesoMax()
                && nuevoCamion.getVolumen() >= t.getRangoVolumenMin()
                && nuevoCamion.getVolumen() <= t.getRangoVolumenMax())
                .findFirst()
                .orElse(null);

        nuevoCamion.setTarifa(tarifa);

        if (tarifa != null) {
            tarifa.getCamiones().add(nuevoCamion);
            tarifa.calcularConsumoCombustibleGralAprox();
            tarifa.calcularCostoBaseKm();
        }

        Camion camionGuardado = camionRepository.save(nuevoCamion);
        Transportista transportista = camionGuardado.getTransportista();
        if (transportista != null) {
            transportista.setCamion(camionGuardado);
            transportistaRepository.save(transportista);
        }

        return toDto(camionGuardado);
    }

    public CamionResponseDto actualizarCamion(Long id, CamionRequestDto dto) {
        Camion camionExistente = camionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Camión {} no encontrado", id);
                    return new RuntimeException("Camión no encontrado");
                });

        // 1. Guardar la tarifa antigua (si existe)
        Tarifa tarifaAntigua = camionExistente.getTarifa();

        // 2. Actualizar las propiedades del camión con los datos del DTO
        camionExistente.setPatente(dto.getPatente());
        camionExistente.setVolumen(dto.getVolumen());
        camionExistente.setPeso(dto.getPeso());
        camionExistente.setCostoBaseKm(dto.getCostoBaseKm());
        camionExistente.setConsumoCombusProm(dto.getConsumoCombusProm());

        // 3. Buscar la nueva tarifa que coincida con los nuevos valores de peso/volumen
        Tarifa tarifaNueva = tarifaRepository.findAll().stream()
                .filter(t -> camionExistente.getPeso() >= t.getRangoPesoMin()
                && camionExistente.getPeso() <= t.getRangoPesoMax()
                && camionExistente.getVolumen() >= t.getRangoVolumenMin()
                && camionExistente.getVolumen() <= t.getRangoVolumenMax())
                .findFirst()
                .orElse(null); // Si no encuentra, queda null

        // 4. Lógica de reasignación y recalculado
        // Chequeamos si la tarifa realmente cambió
        boolean tarifaCambio = (tarifaAntigua != null && tarifaNueva == null)
                || (tarifaAntigua == null && tarifaNueva != null)
                || (tarifaAntigua != null && tarifaNueva != null && !tarifaAntigua.getId().equals(tarifaNueva.getId()));

        if (tarifaCambio) {
            // A) Eliminar el camión de la tarifa antigua y recalcular
            if (tarifaAntigua != null) {
                tarifaAntigua.getCamiones().remove(camionExistente);
                tarifaAntigua.calcularConsumoCombustibleGralAprox();
                tarifaAntigua.calcularCostoBaseKm();
                // No es necesario guardar 'tarifaAntigua' explícitamente si está en el contexto de persistencia.
            }

            // B) Asignar la nueva tarifa al camión
            camionExistente.setTarifa(tarifaNueva);

            // C) Agregar el camión a la nueva tarifa y recalcular
            if (tarifaNueva != null) {
                tarifaNueva.getCamiones().add(camionExistente);
                tarifaNueva.calcularConsumoCombustibleGralAprox();
                tarifaNueva.calcularCostoBaseKm();
                // Tampoco es necesario guardar 'tarifaNueva' explícitamente.
            }
        } else if (tarifaNueva != null && !tarifaCambio) {
            // El camión sigue en la misma tarifa, pero sus valores cambiaron (peso/volumen/costo).
            // Recalculamos la tarifa actual para reflejar los nuevos promedios.
            tarifaNueva.calcularConsumoCombustibleGralAprox();
            tarifaNueva.calcularCostoBaseKm();
        }

        // 5. Actualizar transportista (La lógica de aquí abajo estaba correcta)
        if (dto.getIdTransportista() != null) {
            Transportista transportista = transportistaRepository.findById(dto.getIdTransportista())
                    .orElseThrow(() -> {
                        log.error("Transportista {} no encontrado", dto.getIdTransportista());
                        return new RuntimeException("Transportista no encontrado");
                    });
            camionExistente.setTransportista(transportista);
        } 

        Camion camionActualizado = camionRepository.save(camionExistente);
        return toDto(camionActualizado);
    }

    // Setea disponibilidad=false o disponibilidad=true de un camion
    public CamionResponseDto setearDisponibilidadCamion(Long id, CamionResponseDto responseDto) {
        Camion camion = camionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Camión {} no encontrado", id);
                    return new RuntimeException("Camión no encontrado");
                });

        camion.setDisponibilidad(responseDto.isDisponibilidad());

        Camion camionActualizado = camionRepository.save(camion);

        return toDto(camionActualizado);

    }

    public List<CamionResponseDto> getAll() {
        return camionRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<CamionResponseDto> getCamionesOcupados() {
        return camionRepository.findAll().stream()
                .filter(camion -> camion.isDisponibilidad() == false)
                .map(this::toDto)
                .toList();
    }

    public List<CamionResponseDto> getCamionesDisponibles() {
        return camionRepository.findAll().stream()
                .filter(camion -> camion.isDisponibilidad() == true) // en los atributos booleanos el
                // metodo
                // getDisponibilidad() se llama
                // isDisponibilidad()
                .map(this::toDto)
                .toList();
    }

    public CamionResponseDto obtenerCamionPorId(Long id) {
        Camion camion = camionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Camión {} no encontrado", id);
                    return new RuntimeException("Camión no encontrado");
                });
        return toDto(camion);
    }

    // DTO -> Entity
    private Camion toEntity(CamionRequestDto camionRequestDto) {

        Transportista transportista = null;
        if (camionRequestDto.getIdTransportista() != null) {
            transportista = transportistaRepository.findById(camionRequestDto.getIdTransportista())
                    .orElseThrow(() -> new RuntimeException("Transportista no encontrado con id: "
                    + camionRequestDto.getIdTransportista()));
        }

        return Camion.builder()
                .patente(camionRequestDto.getPatente())
                .volumen(camionRequestDto.getVolumen())
                .peso(camionRequestDto.getPeso())
                .costoBaseKm(camionRequestDto.getCostoBaseKm())
                .consumoCombusProm(camionRequestDto.getConsumoCombusProm())
                .transportista(transportista)
                .build();
    }

    // Entity -> DTO
    public CamionResponseDto toDto(Camion camion) {
        return CamionResponseDto.builder()
                .id(camion.getId())
                .patente(camion.getPatente())
                .volumen(camion.getVolumen())
                .peso(camion.getPeso())
                .costoBaseKm(camion.getCostoBaseKm())
                .consumoCombusProm(camion.getConsumoCombusProm())
                .disponibilidad(camion.isDisponibilidad())
                .idTransportista(camion.getTransportista() != null ? camion.getTransportista().getId()
                        : null)
                .idTarifa(camion.getTarifa() != null ? camion.getTarifa().getId() : null)
                .build();
    }

}
