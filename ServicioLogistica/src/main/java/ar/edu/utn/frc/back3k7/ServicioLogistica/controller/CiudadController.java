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

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.CiudadRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.CiudadResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.service.CiudadService;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ciudades")
@Tag(name = "Ciudades", description = "Operaciones sobre ciudades")
public class CiudadController {

    @Autowired
    private final CiudadService ciudadService;

    @Operation(summary = "Listar todas las ciudades")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping
    public ResponseEntity<List<CiudadResponseDto>> buscarTodas() {
        List<CiudadResponseDto> ciudades = ciudadService.buscarTodas();
        return ResponseEntity.ok(ciudades);
    }

    @Operation(summary = "Registrar ciudad")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<CiudadResponseDto> registrarCiudad(@RequestBody CiudadRequestDto ciudadRequestDto) {
        CiudadResponseDto ciudadResponseDto = ciudadService.registrarCiudad(ciudadRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadResponseDto);
    }

    @Operation(summary = "Actualizar ciudad")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @PutMapping("/{idCiudad}")
    public ResponseEntity<CiudadResponseDto> actualizarCiudad(@PathVariable Long idCiudad,
            @RequestBody CiudadRequestDto ciudadRequestDto) {
        CiudadResponseDto ciudadResponseDto = ciudadService.actualizarCiudad(idCiudad, ciudadRequestDto);
        return ResponseEntity.ok(ciudadResponseDto);
    }
}
