package ar.edu.utn.frc.back3k7.ServicioTransporte.controller;

import ar.edu.utn.frc.back3k7.ServicioTransporte.models.dto.*;
import ar.edu.utn.frc.back3k7.ServicioTransporte.service.CamionService;
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
@RequestMapping("/api/camiones")
@Tag(name = "Camiones", description = "Operaciones sobre camiones")
public class CamionController {

    @Autowired
    private CamionService camionService;

    @Operation(summary = "Crear un camión")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<CamionResponseDto> crearCamion(@RequestBody CamionRequestDto requestDto) {
        CamionResponseDto responseDto = camionService.registrarCamion(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Listar camiones")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping
    public ResponseEntity<List<CamionResponseDto>> getAllCamiones() {
        return ResponseEntity.ok(camionService.getAll());
    }

    @Operation(summary = "Actualizar camión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PutMapping("/{idCamion}")
    public ResponseEntity<CamionResponseDto> actualizarCamion(@PathVariable Long idCamion,
            @RequestBody CamionRequestDto requestDto) {
        CamionResponseDto responseDto = camionService.actualizarCamion(idCamion, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Listar camiones ocupados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/ocupados")
    public ResponseEntity<List<CamionResponseDto>> getCamionesOcupados() {
        return ResponseEntity.ok(camionService.getCamionesOcupados());
    }

    @Operation(summary = "Listar camiones disponibles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<CamionResponseDto>> getCamionesDisponibles() {
        return ResponseEntity.ok(camionService.getCamionesDisponibles());
    }

    @Operation(summary = "Actualizar disponibilidad de camión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping("/disponibles/{idCamion}")
    public ResponseEntity<CamionResponseDto> setearDisponibilidad(@PathVariable Long idCamion,
            @RequestBody CamionResponseDto responseDto) {
        CamionResponseDto response = camionService.setearDisponibilidadCamion(idCamion, responseDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener camión por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CamionResponseDto> obtenerCamionPorId(@PathVariable Long id) {
        CamionResponseDto responseDto = camionService.obtenerCamionPorId(id);
        return ResponseEntity.ok(responseDto);
    }

}
