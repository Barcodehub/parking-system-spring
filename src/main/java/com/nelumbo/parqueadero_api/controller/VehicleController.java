package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleEntryResponseDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitResultDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.services.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/entry")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SuccessResponseDTO<VehicleEntryResponseDTO>> registerEntry(
            @RequestBody @Valid VehicleEntryRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        SuccessResponseDTO<VehicleEntryResponseDTO> response = vehicleService.registerVehicleEntry(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/exit")
    public ResponseEntity<SuccessResponseDTO<VehicleExitResultDTO>> registerVehicleExit(
            @RequestBody @Valid VehicleExitRequestDTO request) {
        return ResponseEntity.ok(vehicleService.registerVehicleExit(request));
    }




}



