package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.EmailResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class EmailController {

    @PostMapping("/send-email")
    public ResponseEntity<EmailResponse> sendEmail(@Valid @RequestBody EmailRequest request) {
        // Simulación de envío de correo
        System.out.println("Simulando envío de email a: " + request.getEmail());
        System.out.println("Detalles: " + request.getMessage());

        EmailResponse response = new EmailResponse(
                "Correo Enviado"
        );

        return ResponseEntity.ok(response);
    }





    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();

            ex.getBindingResult().getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.badRequest().body(errors);
        }
    }

}