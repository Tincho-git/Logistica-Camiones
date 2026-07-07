package ar.edu.utn.frc.back3k7.ServicioTransporte.controller;

import ar.edu.utn.frc.back3k7.ServicioTransporte.client.SolicitudClient;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.Transportista;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.CamionResponseDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.client.dto.TramoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TransportistaRequestDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TransportistaResponseDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.repositories.TransportistaRepository;
import ar.edu.utn.frc.back3k7.ServicioTransporte.service.CamionService;
import ar.edu.utn.frc.back3k7.ServicioTransporte.service.TransportistaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/transportistas")
@Tag(name = "Transportistas", description = "Operaciones sobre transportistas")
public class TransportistaController {

    @Autowired
    private TransportistaService transportistaService;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private CamionService camionService;

    @Autowired
    private SolicitudClient solicitudClient;

    @Operation(summary = "Crear un transportista")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<TransportistaResponseDto> crearTransportista(
            @RequestBody @Valid TransportistaRequestDto requestDto) {
        TransportistaResponseDto responseDto = transportistaService.registrarTransportista(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Listar transportistas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping
    public ResponseEntity<List<TransportistaResponseDto>> listarTransportistas() {
        return ResponseEntity.ok(transportistaService.listarTransportistas());
    }

    @Operation(summary = "Obtener transportista por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransportistaResponseDto> obtenerTransportista(@PathVariable Long id) {
        TransportistaResponseDto dto = transportistaService.obtenerTransportista(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Actualizar transportista")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransportistaResponseDto> actualizarTransportista(@PathVariable Long id,
            @RequestBody @Valid TransportistaRequestDto requestDto) {
        TransportistaResponseDto responseDto = transportistaService.actualizarTransportista(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Obtener camion por transportista")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/camion")
    public ResponseEntity<CamionResponseDto> obtenerCamionPorTransportista(@PathVariable Long id) {
        Transportista t = transportistaRepository.findById(id)
                .orElseThrow(() -> {
                        log.error("Transportista {} no encontrado", id);
                        return new RuntimeException("Transportista no encontrado");
                });

        return ResponseEntity.ok(camionService.toDto(t.getCamion()));
    }

    @Operation(summary = "Obtener tramos asignados a transportista")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/{idTransportista}/tramos-asignados")
    public ResponseEntity<List<TramoResponseDto>> obtenerTramosAsignados(
            @PathVariable Long idTransportista) {
        List<TramoResponseDto> tramos = solicitudClient.obtenerTramosPorTransportista(idTransportista);
        return ResponseEntity.ok(tramos);
    }
}
