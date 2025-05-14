package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.UserRequestDTO;
import com.nelumbo.parqueadero_api.dto.UserResponseDTO;
import com.nelumbo.parqueadero_api.services.ParkingService;
import com.nelumbo.parqueadero_api.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ParkingService parkingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "crear usuario", description = "Crea un usuario de rol SOCIO")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        UserResponseDTO createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }



    @GetMapping("/socio/parkings")
    @Operation(summary = "Parqueaderos del Socio", description = "El socio puede ver listado de los parqueaderos que tiene asociados")
    public List<ParkingResponseDTO> getParkingsBySocio(
            @AuthenticationPrincipal UserDetails userDetails) {

        return parkingService.getParkingsBySocio(userDetails.getUsername());
    }




//de cualquiera rol admin
    @GetMapping("/parkings/{parkingId}/vehicles")
    @Operation(summary = "Vehiculos de un parqueadero especifico", description = "El Admin Puede revisar listado/detalle de vehículos en un parqueadero en especifico")
    public List<AdminVehicleResponseDTO> getVehiclesInParking(
            @PathVariable Integer parkingId,
            @RequestParam(required = false) Boolean activeOnly) {

        return parkingService.getVehiclesInParking(parkingId, activeOnly);
    }

    }








