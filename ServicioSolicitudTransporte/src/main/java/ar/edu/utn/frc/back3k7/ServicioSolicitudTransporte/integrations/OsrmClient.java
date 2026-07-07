package ar.edu.utn.frc.back3k7.ServicioSolicitudTransporte.integrations;

import java.util.List;
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Component
public class OsrmClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public OSRMResponse consultarRuta(double lat1, double lon1, double lat2, double lon2) {
        // OSRM espera lon,lat
        String url = String.format(Locale.US,
                "http://osrm:5000/route/v1/driving/%f,%f;%f,%f?overview=false",
                lon1, lat1, lon2, lat2
        );
        ResponseEntity<OSRMResponse> response = restTemplate.getForEntity(url, OSRMResponse.class);
        return response.getBody();
    }

    @Data
    public static class OSRMResponse {
        private List<Route> routes;
        private String code;

        @Data
        public static class Route {
            private double distance; // metros
            private double duration; // segundos
        }
    }
}