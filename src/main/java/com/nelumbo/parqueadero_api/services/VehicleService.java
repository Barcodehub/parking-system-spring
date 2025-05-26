package com.nelumbo.parqueadero_api.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleEntryResponseDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitResultDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.BusinessRuleException;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.*;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.validation.annotations.ParqueaderoTieneEspacio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.core.context.SecurityContextHolder;
@Service
@RequiredArgsConstructor
@Validated
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingRepository parkingRepository;
    private final UserRepository userRepository;
    private final VehicleHistoryRepository historyRepository;
    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    Logger logger = Logger.getLogger(getClass().getName());


    @Transactional
    public SuccessResponseDTO<VehicleEntryResponseDTO> registerVehicleEntry(@Valid @ParqueaderoTieneEspacio VehicleEntryRequestDTO request, String userEmail) {
        // 1. Normalización de placa
        String placaNormalizada = request.placa().toUpperCase().trim();

        Parking parqueadero = parkingRepository.findById(request.parqueaderoId())
                .orElseThrow(() -> new EntityNotFoundException("Parking not found with id " + request.parqueaderoId()));

        User socio = getCurrentAuthenticatedUser();

        // 5. Crear registro
        Vehicle vehicle = Vehicle.builder()
                .placa(placaNormalizada)
                .parqueadero(parqueadero)
                .socio(socio)
                .fechaIngreso(LocalDateTime.now())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // 6. Notificación
        sendNotification(userEmail, request.placa(), "Correo enviado", parqueadero.getId());

        // 7. Respuesta exitosa
        return new SuccessResponseDTO<>(
                new VehicleEntryResponseDTO(
                        savedVehicle.getId()
                )
        );
    }

    @Transactional
    public SuccessResponseDTO<VehicleExitResultDTO> registerVehicleExit(@Valid VehicleExitRequestDTO request) {        // Normalizar placa
        String placaNormalizada = request.placa().toUpperCase().trim();

        // 3. Vehiculo ya salió
        Vehicle vehicle = vehicleRepository.findByPlacaAndFechaSalidaIsNull(placaNormalizada)
                .orElseThrow(() -> new BusinessRuleException("No se puede Registrar Salida, no existe la placa en el parqueadero"));


        // 3. Calcular costo
        BigDecimal costo = calculateParkingFee(
                vehicle.getFechaIngreso(),
                LocalDateTime.now(),
                vehicle.getParqueadero().getCostoPorHora()
        );

        // 4. Registrar en historial
        VehicleHistory history = createHistoryRecord(vehicle, costo);
        historyRepository.save(history);

        // 4. Eliminar registro activo
        vehicleRepository.delete(vehicle);

        return new SuccessResponseDTO<>(
                new VehicleExitResultDTO(
                        "Salida registrada"
                )
        );
    }

    @Generated
    private User getCurrentAuthenticatedUser() {
        return userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
    @Generated
    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    @Generated
    private BigDecimal calculateParkingFee(LocalDateTime entryTime, LocalDateTime exitTime, BigDecimal hourlyRate) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = Math.max(1, (long) Math.ceil(minutes / 60.0)); // Cobro mínimo: 1 hora // Redondeo hacia arriba
        return hourlyRate.multiply(BigDecimal.valueOf(hours));
    }
    @Generated
    private VehicleHistory createHistoryRecord(Vehicle vehicle, BigDecimal costo) {
        return VehicleHistory.builder()
                .placa(vehicle.getPlaca())
                .fechaIngreso(vehicle.getFechaIngreso())
                .fechaSalida(LocalDateTime.now())
                .parqueadero(vehicle.getParqueadero())
                .socio(vehicle.getSocio())
                .costo(costo)
                .build();
    }
    @Generated
    private void sendNotification(String email, String placa, String message, Integer parqueaderoId) {
        EmailRequest notificationRequest = new EmailRequest(
                email,
                placa.toUpperCase(),
                message,
                parqueaderoId
        );

        try {
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send-email",
                    notificationRequest,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(rawResponse.getBody());
            String responseMessage = rootNode.path("data").path("message").asText();

            logger.log(Level.INFO, "Mensaje: {0}", responseMessage);

        } catch (Exception e) {
            logger.severe("Error al enviar notificación: " + e.getMessage());
        }
    }
}


