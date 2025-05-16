package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitRequestDTO;
import com.nelumbo.parqueadero_api.exception.BusinessException;
import com.nelumbo.parqueadero_api.exception.DuplicateVehicleException;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.*;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingRepository parkingRepository;
    private final UserRepository userRepository;
    private final VehicleHistoryRepository historyRepository;
    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Transactional
    public Map<String, Integer> registerVehicleEntry(VehicleEntryRequestDTO request, String userEmail) {
        // 1. Convertir placa a mayúsculas
        String placaNormalizada = request.placa().toUpperCase().trim();

        // 2. Buscar parqueadero y socio
        Parking parqueadero = parkingRepository.findById(Math.toIntExact(request.parqueaderoId()))
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        User socio = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 3. Verificar Capacidad
        int vehiculosActivos = vehicleRepository.countActiveVehiclesInParking(Long.valueOf(parqueadero.getId()));
        if (vehiculosActivos >= parqueadero.getCapacidad()) {
            throw new BusinessException("El parqueadero ha alcanzado su capacidad máxima");
        }

        // 4. Verificar si el vehículo ya está registrado en CUALQUIER parqueadero (sin salida)
        if (vehicleRepository.existsByPlacaAndFechaSalidaIsNull(placaNormalizada)) {
            throw new DuplicateVehicleException("No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero");
        }

        // 5. Crear y guardar el registro
        Vehicle vehicle = Vehicle.builder()
                .placa(placaNormalizada)
                .parqueadero(parqueadero)
                .socio(socio)
                .fechaIngreso(LocalDateTime.now())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // 6. Enviar notificación por correo
        sendNotification(userEmail, request.placa(), "Correo Enviado", request.parqueaderoId().toString());

        // 7. Retornar respuesta con ID
        return Collections.singletonMap("id", savedVehicle.getId());
    }


    @Transactional
    public Map<String, String> registerVehicleExit(@Valid VehicleEntryRequestDTO request) {
        // Normalizar placa
        String placaNormalizada = request.placa().toUpperCase().trim();

        // 1. Buscar parqueadero
        Parking parqueadero = parkingRepository.findById(Math.toIntExact(request.parqueaderoId()))
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        // 2. Buscar vehículo activo
        Vehicle vehicle = vehicleRepository.findByPlacaAndFechaSalidaIsNull(placaNormalizada)
                .orElseThrow(() -> new BusinessException("No se puede Registrar Salida, no existe la placa en el parqueadero"));


        // 3. Calcular costo
        BigDecimal costo = calculateParkingFee(
                vehicle.getFechaIngreso(),
                LocalDateTime.now(),
                vehicle.getParqueadero().getCostoPorHora()
        );

        // 4. Registrar en historial
        VehicleHistory history = createHistoryRecord(vehicle, costo);
        historyRepository.save(history);

        // 4. Actualizar registro del vehículo con fecha de salida (en lugar de eliminarlo)
//        vehicle.setFechaSalida(LocalDateTime.now());
//        vehicleRepository.save(vehicle);

        // 4. Eliminar registro activo
        vehicleRepository.delete(vehicle);

        return Collections.singletonMap("mensaje", "Salida registrada");
    }



    private BigDecimal calculateParkingFee(LocalDateTime entryTime, LocalDateTime exitTime, BigDecimal hourlyRate) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0); // Redondeo hacia arriba
        return hourlyRate.multiply(BigDecimal.valueOf(hours));
    }

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

    private void sendNotification(String email, String placa, String message, String parqueaderoId) {
        EmailRequest notificationRequest = new EmailRequest(
                email,
                placa.toUpperCase(),
                message,
                parqueaderoId
        );

        try {
            ResponseEntity<EmailResponse> response = restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send-email",
                    notificationRequest,
                    EmailResponse.class);

            // Loggear la respuesta
            System.out.println("Mensaje: " + Objects.requireNonNull(response.getBody()).getMessage());
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
    }
}


