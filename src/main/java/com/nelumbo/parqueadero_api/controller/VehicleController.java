package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitRequestDTO;
import com.nelumbo.parqueadero_api.services.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/entry")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerEntry(
            @RequestBody @Valid VehicleEntryRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        vehicleService.registerVehicleEntry(request, userDetails.getUsername());
    }

    @PostMapping("/exit")
    public ResponseEntity<Map<String, Object>> registerExit(
            @RequestBody @Valid VehicleExitRequestDTO request) {

        BigDecimal costo = vehicleService.registerVehicleExit(request);

        return ResponseEntity.ok(Map.of(
                "message", "Salida registrada exitosamente",
                "placa", request.placa(),
                "costo", costo,
                "fechaSalida", LocalDateTime.now()
        ));
    }

}



