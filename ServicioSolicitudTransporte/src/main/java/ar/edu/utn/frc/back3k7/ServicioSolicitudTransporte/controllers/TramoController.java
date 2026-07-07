package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.TramoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services.TramoService;

@RestController
@RequestMapping("/api/tramos")
@Tag(name = "Tramos", description = "Operaciones sobre tramos")
public class TramoController {

    @Autowired
    private TramoService tramoService;

    @Operation(summary = "Registrar inicio de tramo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @PutMapping("/{idSolicitud}/inicio")
    public ResponseEntity<TramoResponseDto> registrarInicio(@PathVariable Long idSolicitud) {
        TramoResponseDto tramoResponseEntity = tramoService.registrarInicio(idSolicitud); 
        return ResponseEntity.ok(tramoResponseEntity);
    }

    @Operation(summary = "Registrar fin de tramo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @PutMapping("/{idSolicitud}/fin")
    public ResponseEntity<TramoResponseDto> registrarFin(@PathVariable Long idSolicitud) {
        TramoResponseDto tramoResponseEntity = tramoService.registrarFin(idSolicitud); 
        return ResponseEntity.ok(tramoResponseEntity);
    }

    @Operation(summary = "Obtener tramos asignados a transportista")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping("/{idTransportista}/tramos-asignados")
    public ResponseEntity<List<TramoResponseDto>> obtenerTramosAsignados(
            @PathVariable Long idTransportista) {
        return ResponseEntity.ok(tramoService.obtenerTramosPorTransportista(idTransportista));
    }
}

