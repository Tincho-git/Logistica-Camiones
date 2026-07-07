package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.Cliente;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ClienteRequestDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.models.dto.ClienteResponseDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.repositories.ClienteRepository;


@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    
    public Cliente toEntity(ClienteRequestDto cliente){
        return  Cliente.builder()
                .dni(cliente.getDni())
                .tipoDni(cliente.getTipoDni())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .build();
    }



    public ClienteResponseDto toResponseDto(Cliente cliente){
        return  ClienteResponseDto.builder()
                .id(cliente.getId())
                .dni(cliente.getDni())
                .tipoDni(cliente.getTipoDni())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .build();
    }


    public List<ClienteResponseDto> getAll() {
        return clienteRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ClienteResponseDto create(ClienteRequestDto dto){
        Cliente nuevoCliente = toEntity(dto);

        Cliente clienteGuardado = clienteRepository.save(nuevoCliente);

        return toResponseDto(clienteGuardado);

    }

    public ClienteResponseDto getById(Long id){
        Cliente cliente = clienteRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return toResponseDto(cliente);

    }


}
 