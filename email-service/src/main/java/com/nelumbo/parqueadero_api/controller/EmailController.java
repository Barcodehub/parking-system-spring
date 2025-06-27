package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.config.ParkingClient;
import com.nelumbo.parqueadero_api.config.RabbitMQConfig;
import com.nelumbo.parqueadero_api.config.SocioClient;
import com.nelumbo.parqueadero_api.dto.BulletinEmailDTO;
import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.EmailResponse;
import com.nelumbo.parqueadero_api.dto.errors.ErrorDetailDTO;
import com.nelumbo.parqueadero_api.dto.errors.ErrorResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class EmailController {

    Logger logger = Logger.getLogger(getClass().getName());


    private final ParkingClient parkingClient;
    private final RabbitTemplate rabbitTemplate;
    private final SocioClient socioClient;


    @PostMapping("/send-email")
    public ResponseEntity<SuccessResponseDTO<EmailResponse>> sendEmail(@Valid @RequestBody EmailRequest request) {

        if (!parkingClient.parkingExists(request.getParqueaderoId())) {
            SuccessResponseDTO<EmailResponse> errorResponse =
                    new SuccessResponseDTO<>(new EmailResponse("El parqueadero con ID " + request.getParqueaderoId() + " no existe"));
            return ResponseEntity.badRequest().body(errorResponse);
        }
        // Simulación de envío de correo
        logger.info("Simulando envío de email a: " + request.getEmail());
        logger.info("Detalles: " + request.getMessage());

        EmailResponse emailResponse = new EmailResponse("Correo Enviado");
        SuccessResponseDTO<EmailResponse> response = new SuccessResponseDTO<>(emailResponse);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/send-bulletin")
    public ResponseEntity<SuccessResponseDTO<String>> sendBulletin(@RequestBody String mensaje) {
        List<BulletinEmailDTO> socios = socioClient.getSocios();

        for (BulletinEmailDTO socio : socios) {
            socio.setMessage(mensaje);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE, socio);
            logger.info("Mensaje enviado a la cola para: " + socio.getEmail());
        }
        SuccessResponseDTO<String> response = new SuccessResponseDTO<>("Boletín en proceso de envío para " + socios.size() + " usuarios.");

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