package ar.edu.utn.frc.back3k7.ServicioTransporte.client;

import ar.edu.utn.frc.back3k7.ServicioTransporte.client.dto.TramoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudClient {

    @Qualifier("solicitudWebClient")
    private final WebClient solicitudWebClient;

    public List<TramoResponseDto> obtenerTramosPorTransportista(Long idTransportista) {
        TramoResponseDto[] tramos = solicitudWebClient.get()
                .uri("/api/tramos/" + idTransportista + "/tramos-asignados")
                .retrieve()
                .bodyToMono(TramoResponseDto[].class)
                .block();

        return tramos != null ? Arrays.asList(tramos) : List.of();
    }
}
