package com.nelumbo.parqueadero_api.config;

import com.nelumbo.parqueadero_api.dto.BulletinEmailDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class SocioClient {

    private final RestTemplate restTemplate;

    public SocioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BulletinEmailDTO> getSocios() {
        String url = "http://localhost:8080/api/users/socios";
        BulletinEmailDTO[] socios = restTemplate.getForObject(url, BulletinEmailDTO[].class);
        return Arrays.asList(socios);
    }
}
