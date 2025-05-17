package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.UserRequestDTO;
import com.nelumbo.parqueadero_api.dto.UserResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
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
    public ResponseEntity<SuccessResponseDTO<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        SuccessResponseDTO<UserResponseDTO> response = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    



//de cualquiera rol admin
    @GetMapping("/parkings/{parkingId}/vehicles")
    public List<AdminVehicleResponseDTO> getVehiclesInParking(
            @PathVariable Integer parkingId,
            @RequestParam(required = false) Boolean activeOnly) {

        return parkingService.getVehiclesInParking(parkingId);
    }

    }








