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

import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.DepositoRequestDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.models.dto.DepositoResponseDto;
import ar.edu.utn.frc.back3k7.ServicioLogistica.service.DepositoService;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/api/depositos")
@Tag(name = "Depositos", description = "Operaciones sobre depósitos")
public class DepositoController {

    @Autowired
    private final DepositoService depositoService;

    @Operation(summary = "Listar depósitos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping
    public ResponseEntity<List<DepositoResponseDto>> buscarTodos() {
        List<DepositoResponseDto> depositos = depositoService.buscarTodos();
        return ResponseEntity.ok(depositos);
    }

    @Operation(summary = "Buscar depósito por id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found") })
    @GetMapping("/{idDeposito}")
    public ResponseEntity<DepositoResponseDto> buscarPorId(@PathVariable Long idDeposito) {
        DepositoResponseDto depositoResponseDto = depositoService.buscarDepositoPorId(idDeposito);
        return ResponseEntity.ok(depositoResponseDto);
    }

    @Operation(summary = "Buscar depósito por coordenadas")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping("/buscar-por-coordenadas")
    public ResponseEntity<DepositoResponseDto> buscarPorCoordenadas(
            double latitud,
            double longitud) {
        DepositoResponseDto depositoResponseDto = depositoService
                .buscarDepositoPorCoordenadas(latitud, longitud);
        return ResponseEntity.ok(depositoResponseDto);
    }

    @Operation(summary = "Registrar depósito")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Created") })
    @PostMapping
    public ResponseEntity<DepositoResponseDto> registarDeposito(@RequestBody DepositoRequestDto depositoRequestDto) {
        DepositoResponseDto depositoResponseDto = depositoService.registrarDeposito(depositoRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositoResponseDto);
    }

    @Operation(summary = "Actualizar depósito")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @PutMapping("/{idDeposito}")
    public ResponseEntity<DepositoResponseDto> actualizarDeposito(@PathVariable Long idDeposito,
            @RequestBody DepositoRequestDto depositoRequestDto) {
        DepositoResponseDto depositoResponseDto = depositoService.actualizarDeposito(idDeposito, depositoRequestDto);
        return ResponseEntity.ok(depositoResponseDto);
    }
}
