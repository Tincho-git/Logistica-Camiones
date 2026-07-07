package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient transporteWebClient() {
        return WebClient.builder()
            .baseUrl("http://ms-transporte:8080")
            .build();
    }

    @Bean
    public WebClient logisticaWebClient() {
        return WebClient.builder()
            .baseUrl("http://ms-logistica:8080")
            .build();
    }
}
