package ar.edu.utn.frc.back3k7.ApiGateway.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GWConfig {
    @Bean
    public RouteLocator configurarRutas(RouteLocatorBuilder builder,
                                        @Value("${api-gw-tpback.url-microservicio-transporte}") String uriTransportes,
                                        @Value("${api-gw-tpback.url-microservicio-solicitudtransporte}") String uriSolicitudTransportes,
                                        @Value("${api-gw-tpback.url-microservicio-logistica}") String uriLogistica) {
        return builder.routes()
                // Ruteos al Microservicio de Transportes
                .route(p -> p.path("/api/transportistas/**").uri(uriTransportes))
                .route(p -> p.path("/api/camiones/**").uri(uriTransportes))
                .route(p -> p.path("/api/tarifas/**").uri(uriTransportes))
                // Ruteo al Microservicio de Solicitu dtraspos
                .route(p -> p.path("/api/solicitudes/**").uri(uriSolicitudTransportes))
                .route(p -> p.path("/api/rutas/**").uri(uriSolicitudTransportes))
                .route(p -> p.path("/api/contenedores/**").uri(uriSolicitudTransportes))
                .route(p -> p.path("/api/clientes/**").uri(uriSolicitudTransportes))
                .route(p -> p.path("/api/tramos/**").uri(uriSolicitudTransportes))
                // Ruteo al Microservicio de Logistica
                .route(p -> p.path("/api/ciudades/**").uri(uriLogistica))
                .route(p -> p.path("/api/depositos/**").uri(uriLogistica))
                .route(p -> p.path("/api/ubicaciones/**").uri(uriLogistica))
                .route(p -> p.path("/api/estadias-deposito/**").uri(uriLogistica))
                .build();
    }
}
