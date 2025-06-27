package com.nelumbo.parqueadero_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    public ResponseEntity<String> sendBulletin(String message) {
        String url = "http://localhost:8081/api/notifications/send-bulletin";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);
        return restTemplate.postForEntity(url, requestEntity, String.class);
    }
}

