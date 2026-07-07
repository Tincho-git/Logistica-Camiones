package ar.edu.utn.frc.back3k7.ServicioTransporte.service;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Camion;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Tarifa;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TarifaRequestDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TarifaResponseDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.CamionRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.TarifaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    @Autowired
    private CamionRepository camionRepository;

    public TarifaResponseDto registrarTarifa(TarifaRequestDto dto) {
        Tarifa tarifa = toEntity(dto);

        List<Camion> camiones = camionRepository.findAll();

        camiones.stream()
                .filter(c -> {
                    boolean apto = c.getPeso() >= tarifa.getRangoPesoMin()
                            && c.getPeso() <= tarifa.getRangoPesoMax()
                            && c.getVolumen() >= tarifa.getRangoVolumenMin()
                            && c.getVolumen() <= tarifa.getRangoVolumenMax();
                    log.debug("Camion {} apto: {}", c.getId(), apto);
                    return apto;
                })
                .forEach(c -> {
                    tarifa.getCamiones().add(c);
                    c.setTarifa(tarifa);
                });

        tarifa.calcularConsumoCombustibleGralAprox();
        tarifa.calcularCostoBaseKm();
        log.info("Consumo aproximado calculado: {}", tarifa.getConsumoCombustibleGralAprox());
        log.info("CostoBaseKm calculado: {}", tarifa.getCostoBaseKm());

        Tarifa tarifaGuardada = tarifaRepository.save(tarifa);

        return toDto(tarifaGuardada);
    }

    public List<TarifaResponseDto> obtenerTodasLasTarifas() {
        List<Tarifa> tarifas = tarifaRepository.findAll();
        List<TarifaResponseDto> tarifaDtos = new ArrayList<>();

        for (Tarifa tarifa : tarifas) {
            tarifaDtos.add(toDto(tarifa));
        }

        return tarifaDtos;
    }

    private void actualizarCamionesDeTarifa(Tarifa tarifa) {
        // quitar todos los camiones de esta tarifa
        for (Camion c : new ArrayList<>(tarifa.getCamiones())) {
            c.setTarifa(null);
            camionRepository.save(c);
        }
        tarifa.getCamiones().clear();

        // volver a asignar los correctos
        camionRepository.findAll().stream()
                .filter(c -> {
                    boolean apto = c.getPeso() >= tarifa.getRangoPesoMin()
                            && c.getPeso() <= tarifa.getRangoPesoMax()
                            && c.getVolumen() >= tarifa.getRangoVolumenMin()
                            && c.getVolumen() <= tarifa.getRangoVolumenMax();
                    log.debug("Camion {} apto: {}", c.getId(), apto);
                    return apto;
                })
                .forEach(c -> {
                    tarifa.getCamiones().add(c);
                    c.setTarifa(tarifa);
                    camionRepository.save(c);
                });
    }

    public TarifaResponseDto actualizarTarifa(Long id, TarifaRequestDto dto) {
        Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tarifa {} no encontrada", id);
                    return new RuntimeException("Tarifa no encontrada");
                });

        tarifa.setRangoPesoMin(dto.getRangoPesoMin());
        tarifa.setRangoPesoMax(dto.getRangoPesoMax());
        tarifa.setRangoVolumenMin(dto.getRangoVolumenMin());
        tarifa.setRangoVolumenMax(dto.getRangoVolumenMax());
        tarifa.setCostoCombustibleLitro(dto.getCostoCombustibleLitro());

        // reasignar camiones incluidos
        actualizarCamionesDeTarifa(tarifa);
        // recalcular el consumo
        tarifa.calcularConsumoCombustibleGralAprox();
        // recalcular el costoBase
        tarifa.calcularCostoBaseKm();
        log.info("Consumo aproximado calculado: {}", tarifa.getConsumoCombustibleGralAprox());
        log.info("CostoBaseKm calculado: {}", tarifa.getCostoBaseKm());

        return toDto(tarifaRepository.save(tarifa));
    }

    public TarifaResponseDto actualizarParcialTarifa(Long id, TarifaRequestDto dto) {

        Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tarifa {} no encontrada", id);
                    return new RuntimeException("Tarifa no encontrada");
                });

        boolean rangosModificados = false;

        if (dto.getRangoPesoMin() != null) {
            tarifa.setRangoPesoMin(dto.getRangoPesoMin());
            rangosModificados = true;
        }
        if (dto.getRangoPesoMax() != null) {
            tarifa.setRangoPesoMax(dto.getRangoPesoMax());
            rangosModificados = true;
        }
        if (dto.getRangoVolumenMin() != null) {
            tarifa.setRangoVolumenMin(dto.getRangoVolumenMin());
            rangosModificados = true;
        }
        if (dto.getRangoVolumenMax() != null) {
            tarifa.setRangoVolumenMax(dto.getRangoVolumenMax());
            rangosModificados = true;
        }

        if (dto.getCostoCombustibleLitro() != null) {
            tarifa.setCostoCombustibleLitro(dto.getCostoCombustibleLitro());
        }

        // Si cambió algún rango se reasignan camiones y recalcula el consumo y el costo
        if (rangosModificados) {
            actualizarCamionesDeTarifa(tarifa);
            tarifa.calcularConsumoCombustibleGralAprox();
            tarifa.calcularCostoBaseKm();
            log.info("Consumo aproximado calculado: {}", tarifa.getConsumoCombustibleGralAprox());
            log.info("CostoBaseKm calculado: {}", tarifa.getCostoBaseKm());
        }

        Tarifa tarifaActualizada = tarifaRepository.save(tarifa);

        return toDto(tarifaActualizada);
    }

    public TarifaResponseDto obtenerTarifa(Long id) {
        Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tarifa {} no encotrada", id);
                    return new RuntimeException("Tarifa no encontrada");
                });
        return toDto(tarifa);
    }

    // DTO -> Entity
    private Tarifa toEntity(TarifaRequestDto dto) {
        return Tarifa.builder()
                .rangoPesoMin(dto.getRangoPesoMin())
                .rangoPesoMax(dto.getRangoPesoMax())
                .rangoVolumenMin(dto.getRangoVolumenMin())
                .rangoVolumenMax(dto.getRangoVolumenMax())
                .costoCombustibleLitro(dto.getCostoCombustibleLitro())
                .build();
    }

    // Entity -> DTO
    private TarifaResponseDto toDto(Tarifa tarifa) {

        // Paso List<Camion> a List<idCamiones> para agregar al DTO Tarifa
        List<Camion> camiones = tarifa.getCamiones();
        List<Long> idCamiones = new ArrayList<>();

        if (camiones != null) {
            for (Camion camion : camiones) {
                idCamiones.add(camion.getId());
            }
        }

        return TarifaResponseDto.builder()
                .id(tarifa.getId())
                .rangoPesoMin(tarifa.getRangoPesoMin())
                .rangoPesoMax(tarifa.getRangoPesoMax())
                .rangoVolumenMin(tarifa.getRangoVolumenMin())
                .rangoVolumenMax(tarifa.getRangoVolumenMax())
                .costoCombustibleLitro(tarifa.getCostoCombustibleLitro())
                .consumoCombustibleGralAprox(tarifa.getConsumoCombustibleGralAprox())
                .costoBaseKm(tarifa.getCostoBaseKm())
                .idCamiones(idCamiones)
                .build();
    }
}
