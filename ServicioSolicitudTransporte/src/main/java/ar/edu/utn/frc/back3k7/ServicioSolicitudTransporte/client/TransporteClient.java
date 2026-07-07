package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.CamionClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.TarifaClientDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransporteClient {

    @Qualifier("transporteWebClient")
    private final WebClient transporteWebClient;

    public TarifaClientDto obtenerTarifaPorId(Long idTarifa) {
        return transporteWebClient.get()
                .uri("/api/tarifas/" + idTarifa)
                .retrieve()
                .bodyToMono(TarifaClientDto.class)
                .block(); // hace que sea sincrónico
    }

    // obtener todas las tarifas
    public List<TarifaClientDto> obtenerTodasLasTarifas() {
        return transporteWebClient.get()
                .uri("/api/tarifas")
                .retrieve()
                .bodyToFlux(TarifaClientDto.class)
                .collectList()
                .block(); // hace que sea sincrónico
    }

    // Llama al PATCH de camiones para setear disponibilidad=false o
    // disponibilidad=true
    public void actualizarCamion(CamionClientDto camionClientDto) {
        transporteWebClient.patch()
                .uri("/api/camiones/disponibles/" + camionClientDto.getId())
                .bodyValue(camionClientDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block(); // hace que sea sincrónico
    }

    // Llama al GET de camiones por id
    public CamionClientDto obtenerCamionPorId(Long idCamion) {
        return transporteWebClient.get()
                .uri("/api/camiones/" + idCamion)
                .retrieve()
                .bodyToMono(CamionClientDto.class)
                .block(); // hace que sea sincrónico
    }

    /***
     *
     */

    public CamionClientDto obtenerCamionPorTransportista(Long idTransportista) {
        return transporteWebClient.get()
                .uri("/api/transportistas/" + idTransportista + "/camion")
                .retrieve()
                .bodyToMono(CamionClientDto.class)
                .block();
    }

}
