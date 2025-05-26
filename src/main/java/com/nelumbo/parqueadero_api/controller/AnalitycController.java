package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.*;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.services.AnalitycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analityc")
@RequiredArgsConstructor
public class AnalitycController {

    private final AnalitycService reportService;

    @GetMapping("/vehicles/top-global")
    public ResponseEntity<SuccessResponseDTO<List<VehicleFrequencyDTO>>> getTop10VehiclesGlobal() {
        SuccessResponseDTO<List<VehicleFrequencyDTO>> response = reportService.getTop10MostFrequentVehicles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parkings/{id}/vehicles/top")
    public ResponseEntity<SuccessResponseDTO<List<VehicleFrequencyDTO>>> getTop10VehiclesByParking(
            @PathVariable Integer id) {
        SuccessResponseDTO<List<VehicleFrequencyDTO>> response =
                reportService.getTop10MostFrequentVehiclesByParking(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parkings/{id}/vehicles/first-time")
    public ResponseEntity<SuccessResponseDTO<List<VehicleDTO>>> getFirstTimeVehicles(@PathVariable Integer id) {
        SuccessResponseDTO<List<VehicleDTO>> response = reportService.getFirstTimeVehicles(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parkings/{id}/earnings")
    public ResponseEntity<SuccessResponseDTO<ParkingEarningsDTO>> getParkingEarnings(@PathVariable Integer id) {
        SuccessResponseDTO<ParkingEarningsDTO> response = reportService.getParkingEarnings(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/socios/top-earnings")
    public ResponseEntity<SuccessResponseDTO<List<SocioEarningsDTO>>> getTop3SociosByEarnings() {
        SuccessResponseDTO<List<SocioEarningsDTO>> response = reportService.getTop3SociosByWeeklyEarnings();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parkings/top-earnings")
    public ResponseEntity<SuccessResponseDTO<List<ParkingTopEarningsDTO>>> getTop3ParkingsByEarnings() {
        SuccessResponseDTO<List<ParkingTopEarningsDTO>> response = reportService.getTop3ParkingsByWeeklyEarnings();
        return ResponseEntity.ok(response);
    }


}
