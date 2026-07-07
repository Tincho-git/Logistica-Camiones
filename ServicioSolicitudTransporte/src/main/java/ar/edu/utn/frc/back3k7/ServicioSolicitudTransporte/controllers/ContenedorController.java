package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ContenedorEstadoDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ContenedorRequestDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ContenedorResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services.ContenedorService;

@RestController
@RequestMapping("/api/contenedores")
@Tag(name = "Contenedores", description = "Operaciones sobre contenedores")
public class ContenedorController {

    @Autowired
    private ContenedorService contenedorService;

    @Operation(summary = "Crear contenedor")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping
    public ResponseEntity<ContenedorResponseDto> create(@RequestBody ContenedorRequestDto requestDto) {
        ContenedorResponseDto responseDto = contenedorService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Listar contenedores")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping
    public ResponseEntity<List<ContenedorResponseDto>> findAll() {
        return ResponseEntity.ok(contenedorService.getAll());
    }

    @Operation(summary = "Obtener contenedor por id")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping("/{id}")
    public ResponseEntity<ContenedorResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(contenedorService.getById(id));
    }

    // muestra el estado actual del contenedor junto con su historial de estados en
    // orden cronologico
    @Operation(summary = "Obtener estado de contenedor")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping("/{idContenedor}/estado")
    public ResponseEntity<ContenedorEstadoDto> obtenerEstado(@PathVariable Long idContenedor) {
        ContenedorEstadoDto response = contenedorService.obtenerEstado(idContenedor);
        return ResponseEntity.ok(response);
    }

    // muestra todos los contenedores que no esten en estado ENTREGADO
    // y permite filtrar por estado actual a traves de la url
    @Operation(summary = "Listar contenedores pendientes")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping("/pendientes")
    public ResponseEntity<List<ContenedorEstadoDto>> obtenerContenedoresNoEntregados(
            @RequestParam(required = false) String estado) {
        List<ContenedorEstadoDto> response = contenedorService.obtenerPendientes(estado);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar contenedor")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @PutMapping("/{idContenedor}")
    public ResponseEntity<ContenedorResponseDto> actualizarContenedor(@PathVariable Long idContenedor,
            @RequestBody ContenedorRequestDto requestDto) {
        ContenedorResponseDto responseDto = contenedorService.actualizarContenedor(idContenedor, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
