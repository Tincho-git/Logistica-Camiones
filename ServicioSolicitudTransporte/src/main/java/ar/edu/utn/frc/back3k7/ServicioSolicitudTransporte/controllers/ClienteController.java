package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.controllers;


import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ClienteRequestDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ClienteResponseDto;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operaciones sobre clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Crear cliente")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Created")})
    @PostMapping
    public ResponseEntity<ClienteResponseDto> create(@RequestBody ClienteRequestDto requestDto) {
        ClienteResponseDto responseDto = clienteService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Listar clientes")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> findAll() {
        return ResponseEntity.ok(clienteService.getAll());
    }

    @Operation(summary = "Obtener cliente por id")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.getById(id));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ClienteResponseDto> actualizarCliente(@PathVariable Long id,@RequestBody ClienteRequestDto requestDto) {
//        return ResponseEntity.ok(clienteService.actualizarProfesional(id, requestDto));
//    }

}