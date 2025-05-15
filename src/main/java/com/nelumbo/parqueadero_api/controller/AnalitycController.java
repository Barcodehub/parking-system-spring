package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.*;
import com.nelumbo.parqueadero_api.services.AnalitycService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    public List<VehicleFrequencyDTO> getTop10VehiclesGlobal() {
        return reportService.getTop10MostFrequentVehicles();
    }

    @GetMapping("/parkings/{id}/vehicles/top")
    public List<VehicleFrequencyDTO> getTop10VehiclesByParking(@PathVariable Long id) {
        return reportService.getTop10MostFrequentVehiclesByParking(id);
    }

    @GetMapping("/parkings/{id}/vehicles/first-time")
    public List<VehicleDTO> getFirstTimeVehicles(@PathVariable Long id) {
        return reportService.getFirstTimeVehicles(id);
    }

    @GetMapping("/parkings/{id}/earnings")
    public ParkingEarningsDTO getParkingEarnings(@PathVariable Long id) {
        return reportService.getParkingEarnings(id);
    }

    @GetMapping("/socios/top-earnings")
    public List<SocioEarningsDTO> getTop3SociosByEarnings() {
        return reportService.getTop3SociosByWeeklyEarnings();
    }

    @GetMapping("/parkings/top-earnings")
    public List<ParkingTopEarningsDTO> getTop3ParkingsByEarnings() {
        return reportService.getTop3ParkingsByWeeklyEarnings();
    }


}
