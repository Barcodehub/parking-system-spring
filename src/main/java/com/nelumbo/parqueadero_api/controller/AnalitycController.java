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
    @Operation(summary = "TopGlobal", description = "Top 10 vehículos que más veces se han registrado en los diferentes")
    public List<VehicleFrequencyDTO> getTop10VehiclesGlobal() {
        return reportService.getTop10MostFrequentVehicles();
    }

    @GetMapping("/parkings/{id}/vehicles/top")
    @Operation(summary = "TopRegistro", description = "Top 10 vehículos que más veces se han registrado en un parqueadero y cuantas veces han sido")
    public List<VehicleFrequencyDTO> getTop10VehiclesByParking(@PathVariable Long id) {
        return reportService.getTop10MostFrequentVehiclesByParking(id);
    }

    @GetMapping("/parkings/{id}/vehicles/first-time")
    @Operation(summary = "Vehiculos por primera vez", description = "Verificar de los vehículos parqueados cuales son por primera vez en ese parqueadero.")
    public List<VehicleDTO> getFirstTimeVehicles(@PathVariable Long id) {
        return reportService.getFirstTimeVehicles(id);
    }

    @GetMapping("/parkings/{id}/earnings")
    @Operation(summary = "Ganancias mes/semana/año/hoy", description = "Obtener las ganancias de hoy, esta semana, este mes, este año de un parqueadero en específico.")
    public ParkingEarningsDTO getParkingEarnings(@PathVariable Long id) {
        return reportService.getParkingEarnings(id);
    }

    @GetMapping("/socios/top-earnings")
    @Operation(summary = "Top3 Socios con mas ganancias semanal", description = "Top 3 de los socios con más ingresos de vehículos en la semana actual y mostrar la cantidad de vehículos")
    public List<SocioEarningsDTO> getTop3SociosByEarnings() {
        return reportService.getTop3SociosByWeeklyEarnings();
    }

    @GetMapping("/parkings/top-earnings")
    @Operation(summary = "Top3 Parqueaderos con mas ganancias semanal", description = "Top 3 de los parqueaderos con mayor ganancia en la semana.")
    public List<ParkingTopEarningsDTO> getTop3ParkingsByEarnings() {
        return reportService.getTop3ParkingsByWeeklyEarnings();
    }


}
