package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.DepositoClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.EstadiaDepositoClientDto;
import ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.client.dto.UbicacionClientDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogisticaClient {

        @Qualifier("logisticaWebClient")
        private final WebClient logisticaWebClient;

        public UbicacionClientDto buscarUbicacionPorCoordenadas(double latitud, double longitud, String ciudad,
                        String direccion) {
                return logisticaWebClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/ubicaciones/buscar-por-coordenadas")
                                                .queryParam("latitud", latitud)
                                                .queryParam("longitud", longitud)
                                                .queryParam("ciudad", ciudad)
                                                .queryParam("direccion", direccion)
                                                .build())
                                .retrieve()
                                .bodyToMono(UbicacionClientDto.class)
                                .block(); // hace que sea sincrónico
        }

        public DepositoClientDto buscarDepositoPorCoordenadas(double latitud, double longitud) {
                return logisticaWebClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/depositos/buscar-por-coordenadas")
                                                .queryParam("latitud", latitud)
                                                .queryParam("longitud", longitud)
                                                .build())
                                .retrieve()
                                .bodyToMono(DepositoClientDto.class)
                                .block(); // hace que sea sincrónico
        }

        public DepositoClientDto buscarDepositoPorId(Long id) {
                return logisticaWebClient.get()
                                .uri("/api/depositos/" + id)
                                .retrieve()
                                .bodyToMono(DepositoClientDto.class)
                                .block(); // hace que sea sincrónico
        }

        public List<DepositoClientDto> obtenerDepositos() {
                DepositoClientDto[] depositos = logisticaWebClient.get()
                                .uri("/api/depositos")
                                .retrieve()
                                .bodyToMono(DepositoClientDto[].class)
                                .block(); // hace que sea sincrónico

                return depositos != null ? Arrays.asList(depositos) : List.of();
        }

        public EstadiaDepositoClientDto crearEstadiaDeposito(EstadiaDepositoClientDto estadia) {
                return logisticaWebClient.post()
                                .uri("/api/estadias-deposito")
                                .bodyValue(estadia)
                                .retrieve()
                                .bodyToMono(EstadiaDepositoClientDto.class)
                                .block(); // hace que sea sincrónico

        }

        public EstadiaDepositoClientDto buscarEstadiaDepositoPorId(Long id) {
                return logisticaWebClient.get()
                                .uri("/api/estadias-deposito/" + id)
                                .retrieve()
                                .bodyToMono(EstadiaDepositoClientDto.class)
                                .block(); // hace que sea sincrónico
        }

        public void actualizarEstadiaDeposito(EstadiaDepositoClientDto estadia) {
                logisticaWebClient.patch()
                                .uri("/api/estadias-deposito/" + estadia.getId())
                                .bodyValue(estadia)
                                .retrieve()
                                .bodyToMono(Void.class)
                                .block(); // hace que sea sincrónico
        }

        public List<EstadiaDepositoClientDto> buscarEstadiasActivasPorDeposito(Long depositoId) {
                EstadiaDepositoClientDto[] estadias = logisticaWebClient.get()
                                .uri("/api/estadias-deposito/deposito/" + depositoId + "/activas")
                                .retrieve()
                                .bodyToMono(EstadiaDepositoClientDto[].class)
                                .block(); // sincrónico

                return estadias != null ? Arrays.asList(estadias) : List.of();
        }

        public long obtenerCantidadDiasEstadia(Long idEstadia) {
                return logisticaWebClient.get()
                                .uri("/api/estadias-deposito/" + idEstadia + "/cantidad-dias-estadia")
                                .retrieve()
                                .bodyToMono(Long.class)
                                .block(); // sincrónico
        }
}
