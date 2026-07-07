package ar.edu.utn.frc.back3k7.ServicioTransporte.controller;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TarifaRequestDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.TarifaResponseDto;
import ar.edu.utn.frc.back3k7.ServicioTransporte.service.TarifaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tarifas")
@Tag(name = "Tarifas", description = "Operaciones relacionadas con las tarifas")
public class TarifaController {

    @Autowired
    private TarifaService tarifaService;

    @Operation(summary = "Obtener todas las tarifas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<TarifaResponseDto>> obtenerTodasLasTarifas() {
        return ResponseEntity.ok(tarifaService.obtenerTodasLasTarifas());
    }

    @Operation(summary = "Registrar una nueva tarifa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<TarifaResponseDto> registrarTarifa(@RequestBody @Valid TarifaRequestDto tarifaRequestDto) {
        TarifaResponseDto tarifaResponseDto = tarifaService.registrarTarifa(tarifaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaResponseDto);
    }

    @Operation(summary = "Actualizar tarifa completa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{idTarifa}")
    public ResponseEntity<TarifaResponseDto> actualizarTarifa(
            @PathVariable Long idTarifa,
            @RequestBody @Valid TarifaRequestDto tarifaRequestDto) {
        TarifaResponseDto tarifaResponseDto = tarifaService.actualizarTarifa(idTarifa, tarifaRequestDto);
        return ResponseEntity.ok(tarifaResponseDto);
    }

    @Operation(summary = "Actualizar tarifa parcialmente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping("/{idTarifa}")
    public ResponseEntity<TarifaResponseDto> actualizarParcialTarifa(
            @PathVariable Long idTarifa,
            @RequestBody TarifaRequestDto tarifaRequestDto) {
        TarifaResponseDto tarifaResponseDto = tarifaService.actualizarParcialTarifa(idTarifa, tarifaRequestDto);
        return ResponseEntity.ok(tarifaResponseDto);
    }

    @Operation(summary = "Obtener tarifa por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{idTarifa}")
    public ResponseEntity<TarifaResponseDto> obtenerTarifa(@PathVariable Long idTarifa) {
        TarifaResponseDto tarifaResponseDto = tarifaService.obtenerTarifa(idTarifa);
        return ResponseEntity.ok(tarifaResponseDto);
    }
}
