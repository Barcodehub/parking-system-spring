package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitRequestDTO;
import com.nelumbo.parqueadero_api.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/entry")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerEntry(
            @RequestBody @Valid VehicleEntryRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Integer> response = vehicleService.registerVehicleEntry(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }



    @PostMapping("/exit")
    public ResponseEntity<?> registerVehicleExit(@RequestBody @Valid VehicleExitRequestDTO request) {
        Map<String, String> response = vehicleService.registerVehicleExit(request);
        return ResponseEntity.ok(response);
    }




}



