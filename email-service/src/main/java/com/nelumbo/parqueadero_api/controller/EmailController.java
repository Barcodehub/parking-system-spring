package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.EmailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class EmailController {

    @PostMapping("/send-email")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailRequest request) {
        // Simulación de envío de correo
        System.out.println("Simulando envío de email a: " + request.getEmail());
        System.out.println("Detalles: " + request.getMessage());

        EmailResponse response = new EmailResponse(
                "Correo Enviado"
        );

        return ResponseEntity.ok(response);
    }
}