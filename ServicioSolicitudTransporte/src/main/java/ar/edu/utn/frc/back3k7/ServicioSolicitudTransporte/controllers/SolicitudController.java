package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.controllers;

import java.util.List;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services.SolicitudService;

@RestController
@RequestMapping("/api/solicitudes")
@Tag(name = "Solicitudes", description = "Operaciones relacionadas con las solicitudes de transporte")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;


    @PostMapping
    @Operation(summary = "Crear solicitud de transporte")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    public ResponseEntity<SolicitudResponseDto> create(@RequestBody SolicitudRequestDto requestDto) {
        SolicitudResponseDto responseDto = solicitudService.crearSolicitud(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @GetMapping
    @Operation(summary = "Listar solicitudes")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<SolicitudResponseDto>> findAll() {
        return ResponseEntity.ok(solicitudService.getAll());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de solicitud")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<SolicitudDetalleDto> obtenerSolicitud(@PathVariable Long id) {
        SolicitudDetalleDto response = solicitudService.obtenerSolicitud(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/estado")
    @Operation(summary = "Obtener estado de solicitud")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<SolicitudEstadoDto> obtenerEstado(@PathVariable Long id) {
        SolicitudEstadoDto response = solicitudService.obtenerEstado(id);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{idSolicitud}/asignar-camion/{idCamion}")
    @Operation(summary = "Asignar camión a solicitud")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<TramoResponseDto> asignarCamion(@PathVariable Long idSolicitud, @PathVariable Long idCamion) {
        TramoResponseDto response = solicitudService.asignarCamion(idSolicitud,idCamion);
        return ResponseEntity.ok(response);
    }
}
