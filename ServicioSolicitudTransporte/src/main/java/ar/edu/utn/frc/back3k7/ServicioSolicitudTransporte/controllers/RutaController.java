 package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.RutaResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.RutaTentativaDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services.RutaService;

@RestController
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Operaciones sobre rutas")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    @Operation(summary = "Obtener rutas tentativas para una solicitud")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping("/tentativa/{idSolicitud}")
    public ResponseEntity<List<RutaTentativaDto>> obtenerRutasTentativas(
            @PathVariable Long idSolicitud) {
        List<RutaTentativaDto> response = rutaService.obtenerRutasTentativas(idSolicitud);
        return ResponseEntity.ok(response);
    }

    
    @Operation(summary = "Consultar ruta por solicitud")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping("/{idSolicitud}")
    public ResponseEntity<RutaResponseDto> consultarRuta(@PathVariable Long idSolicitud) {
        RutaResponseDto response = rutaService.consultarRuta(idSolicitud);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Seleccionar ruta para una solicitud")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "400", description = "Bad Request")})
    @PostMapping("/solicitud/{idSolicitud}/seleccionar/{idRuta}")
    public ResponseEntity<RutaResponseDto> seleccionarRuta(@PathVariable Long idSolicitud, @PathVariable Long idRuta) {
        RutaResponseDto response = rutaService.seleccionarRutaParaSolicitud(idSolicitud, idRuta);
        return ResponseEntity.ok(response);
    }


}
