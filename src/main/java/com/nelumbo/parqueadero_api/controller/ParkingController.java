package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.services.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkings")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<ParkingResponseDTO>> createParking(
            @RequestBody @Valid ParkingRequestDTO request) {
        SuccessResponseDTO<ParkingResponseDTO> response = parkingService.createParking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    public SuccessResponseDTO<ParkingResponseDTO> getParkingById(@PathVariable Integer id) {
        return parkingService.getParkingById(id);
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<ParkingResponseDTO>>> getAllParkings(
            @AuthenticationPrincipal UserDetails userDetails) { // Inyecta el usuario autenticado
        SuccessResponseDTO<List<ParkingResponseDTO>> response =
                parkingService.getAllParkingsFiltered(userDetails); // Lógica delegada al servicio
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<ParkingResponseDTO>> updateParking(
            @PathVariable Integer id,
            @RequestBody @Valid ParkingRequestDTO request) {
        SuccessResponseDTO<ParkingResponseDTO> response = parkingService.updateParking(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Void>> deleteParking(@PathVariable Integer id) {
        SuccessResponseDTO<Void> response = parkingService.deleteParking(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/parkings/{parkingId}/vehicles")
    public ResponseEntity<SuccessResponseDTO<List<AdminVehicleResponseDTO>>> getVehiclesInParking(
            @PathVariable Integer parkingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        SuccessResponseDTO<List<AdminVehicleResponseDTO>> response =
                parkingService.getVehiclesInParking(parkingId, userDetails);

        return ResponseEntity.ok(response);
    }

}