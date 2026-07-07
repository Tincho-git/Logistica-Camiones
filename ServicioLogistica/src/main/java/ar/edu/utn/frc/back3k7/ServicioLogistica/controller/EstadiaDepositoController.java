package ar.edu.utn.frc.back3k7.ServicioLogistica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.EstadiaDepositoRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.EstadiaDepositoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.repositories.EstadiaDepositoRepository;
import ar.edu.utn.frc.back3k7.ServicioLogistica.service.EstadiaDepositoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/estadias-deposito")
@Tag(name = "EstadiasDeposito", description = "Operaciones sobre estadías en depósitos")
public class EstadiaDepositoController {

    @Autowired
    private EstadiaDepositoService estadiaDepositoService;

    @Autowired
    private EstadiaDepositoRepository estadiaDepositoRepository;

    @Operation(summary = "Buscar estadía por id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found") })
    @GetMapping("/{idEstadia}")
    public ResponseEntity<EstadiaDepositoResponseDto> buscarPorId(@PathVariable Long idEstadia) {
        EstadiaDepositoResponseDto estadiaDepositoResponseDto = estadiaDepositoService
                .buscarEstadiaDepositoPorId(idEstadia);
        return ResponseEntity.ok(estadiaDepositoResponseDto);
    }

    @Operation(summary = "Registrar estadía en depósito")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Created") })
    @PostMapping
    public ResponseEntity<EstadiaDepositoResponseDto> registrarEstadiaDeposito(
            @RequestBody EstadiaDepositoRequestDto estadiaDepositoRequestDto) {
        EstadiaDepositoResponseDto estadiaDepositoResponseDto = estadiaDepositoService
                .registrarEstadia(estadiaDepositoRequestDto);
        return ResponseEntity.ok(estadiaDepositoResponseDto);
    }

    @Operation(summary = "Actualizar fecha/hora de salida de estadía")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @PatchMapping("/{idEstadia}")
    public ResponseEntity<EstadiaDepositoResponseDto> actualizarFechaHoraSalidaEstadia(@PathVariable Long idEstadia,
            @RequestBody EstadiaDepositoRequestDto estadiaDepositoRequestDto) {
        EstadiaDepositoResponseDto estadiaDepositoResponseDto = estadiaDepositoService
                .actualizarFechaHoraSalidaEstadia(idEstadia, estadiaDepositoRequestDto);
        return ResponseEntity.ok(estadiaDepositoResponseDto);
    }

    @Operation(summary = "Obtener estadías activas por depósito")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping("/deposito/{id}/activas")
    public List<EstadiaDepositoResponseDto> obtenerEstadiasActivasPorDeposito(@PathVariable Long id) {
        return estadiaDepositoRepository.findByDepositoIdAndFechaHoraSalidaIsNull(id)
                .stream()
                .map(estadiaDepositoService::toDto)
                .toList();
    }

    @Operation(summary = "Obtener cantidad de dias que un contenedor estuvo en la estadía")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping("/{idEstadia}/cantidad-dias-estadia")
    public long obtenerCantidadDiasEstadia(@PathVariable Long idEstadia) {
        return estadiaDepositoService.calcularDiasEstadia(idEstadia);
    }

}
