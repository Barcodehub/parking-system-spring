package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.EmailResponse;
import com.nelumbo.parqueadero_api.dto.errors.ErrorDetailDTO;
import com.nelumbo.parqueadero_api.dto.errors.ErrorResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class EmailController {

    Logger logger = Logger.getLogger(getClass().getName());

    @PostMapping("/send-email")
    public ResponseEntity<SuccessResponseDTO<EmailResponse>> sendEmail(@Valid @RequestBody EmailRequest request) {
        // Simulación de envío de correo
        logger.info("Simulando envío de email a: " + request.getEmail());
        logger.info("Detalles: " + request.getMessage());

        EmailResponse emailResponse = new EmailResponse("Correo Enviado");
        SuccessResponseDTO<EmailResponse> response = new SuccessResponseDTO<>(emailResponse);

        return ResponseEntity.ok(response);
    }





    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
            List<ErrorDetailDTO> errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> new ErrorDetailDTO(
                            "400",
                            error.getDefaultMessage(),
                            error.getField()
                    ))
                    .toList();

            ErrorResponseDTO response = new ErrorResponseDTO(null, errors);
            return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGeneralExceptions(Exception ex) {
            ErrorDetailDTO error = new ErrorDetailDTO(
                    "INTERNAL_ERROR",
                    ex.getMessage(),
                    null
            );
            ErrorResponseDTO response = new ErrorResponseDTO(null, List.of(error));
            return ResponseEntity.internalServerError().body(response);
        }
    }

}