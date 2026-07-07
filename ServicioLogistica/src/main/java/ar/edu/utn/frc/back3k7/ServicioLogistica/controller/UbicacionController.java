package ar.edu.utn.frc.back3k7.ServicioLogistica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.UbicacionRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.UbicacionResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.service.UbicacionService;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ubicaciones")
@Tag(name = "Ubicaciones", description = "Operaciones sobre ubicaciones")
public class UbicacionController {

    @Autowired
    private final UbicacionService ubicacionService;

    @Operation(summary = "Listar ubicaciones")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping
    public ResponseEntity<List<UbicacionResponseDto>> buscarTodas() {
        List<UbicacionResponseDto> ubicaciones = ubicacionService.buscarTodas();
        return ResponseEntity.ok(ubicaciones);
    }

    @Operation(summary = "Buscar ubicación por id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found") })
    @GetMapping("/{idUbicacion}")
    public ResponseEntity<UbicacionResponseDto> buscarPorId(@PathVariable Long idUbicacion) {
        UbicacionResponseDto ubicacionResponseDto = ubicacionService.buscarUbicacionPorId(idUbicacion);
        return ResponseEntity.ok(ubicacionResponseDto);
    }

    @Operation(summary = "Buscar ubicación por coordenadas")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping("/buscar-por-coordenadas")
    public ResponseEntity<UbicacionResponseDto> buscarPorCoordenadas(
            double latitud,
            double longitud,
            String ciudad,
            String direccion) {
        UbicacionResponseDto ubicacionResponseDto = ubicacionService
                .buscarUbicacionPorCoordenadas(latitud, longitud, ciudad, direccion);
        return ResponseEntity.ok(ubicacionResponseDto);
    }

    @Operation(summary = "Registrar ubicación")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Created") })
    @PostMapping
    public ResponseEntity<UbicacionResponseDto> registarUbicacion(
            @RequestBody UbicacionRequestDto ubicacionRequestDto) {
        UbicacionResponseDto ubicacionResponseDto = ubicacionService.registrarUbicacion(ubicacionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionResponseDto);
    }

    @Operation(summary = "Actualizar ubicación")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @PutMapping("/{idUbicacion}")
    public ResponseEntity<UbicacionResponseDto> actualizarUbicacion(@PathVariable Long idUbicacion,
            @RequestBody UbicacionRequestDto ubicacionRequestDto) {
        UbicacionResponseDto ubicacionResponseDto = ubicacionService.actualizarUbicaicon(idUbicacion,
                ubicacionRequestDto);
        return ResponseEntity.ok(ubicacionResponseDto);
    }
}
