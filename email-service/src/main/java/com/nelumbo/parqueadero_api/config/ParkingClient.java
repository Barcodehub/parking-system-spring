package com.nelumbo.parqueadero_api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class ParkingClient {

    private final RestTemplate restTemplate;

    public ParkingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean parkingExists(Integer parkingId) {
        try {
            String url = "http://localhost:8080/api/parkings/email/" + parkingId;
            restTemplate.getForObject(url, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            // ee
            return false;
        }
    }
}
