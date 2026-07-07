package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Transporte y Logística")
                        .version("1.0")
                        .description("Documentación de la API para gestión de transportes, solicitudes y logística")
                        .contact(new Contact()
                                .name("Grupo 59")));
    }
}