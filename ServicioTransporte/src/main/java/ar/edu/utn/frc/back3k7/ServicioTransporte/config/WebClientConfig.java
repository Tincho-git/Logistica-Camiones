package ar.edu.utn.frc.back3k7.ServicioTransporte.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient solicitudWebClient() {
        return WebClient.builder()
                .baseUrl("http://ms-solicitudtransporte:8080")
                .build();
    }

}

