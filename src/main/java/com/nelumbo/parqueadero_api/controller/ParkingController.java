package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.services.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parkings")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingResponseDTO createParking(@RequestBody @Valid ParkingRequestDTO request) {
        return parkingService.createParking(request);
    }

    @GetMapping("/{id}")
    public ParkingResponseDTO getParkingById(@PathVariable Integer id) {
        return parkingService.getParkingById(id);
    }

    @GetMapping
    public List<ParkingResponseDTO> getAllParkings(
            @RequestParam(required = false) Integer socioId) {
        return parkingService.getAllParkings(Optional.ofNullable(socioId));
    }

    @PutMapping("/{id}")
    public ParkingResponseDTO updateParking(
            @PathVariable Integer id,
            @RequestBody @Valid ParkingRequestDTO request) {
        return parkingService.updateParking(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParking(@PathVariable Integer id) {
        parkingService.deleteParking(Math.toIntExact(id));
    }
}