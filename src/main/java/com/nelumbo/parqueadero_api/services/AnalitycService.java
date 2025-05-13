package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.*;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalitycService {

    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final VehicleRepository vehicleRepository;

    public List<VehicleFrequencyDTO> getTop10MostFrequentVehicles() {
        return vehicleHistoryRepository.findTop10MostFrequentVehicles().stream()
                .map(result -> new VehicleFrequencyDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .toList();
    }

    public List<VehicleFrequencyDTO> getTop10MostFrequentVehiclesByParking(Long parkingId) {
        return vehicleHistoryRepository.findTop10ByParkingId(parkingId).stream()
                .map(result -> new VehicleFrequencyDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .toList();
    }

    public List<VehicleDTO> getFirstTimeVehicles(Long parkingId) {
        List<String> existingVehicles = vehicleHistoryRepository.findPlacasByParking(parkingId);
        return vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(Math.toIntExact(parkingId)).stream()
                .filter(v -> !existingVehicles.contains(v.getPlaca()))
                .map(this::convertToDTO)
                .toList();
    }

    public ParkingEarningsDTO getParkingEarnings(Long parkingId) {
        return new ParkingEarningsDTO(
                Optional.ofNullable(vehicleHistoryRepository.findTodayEarnings(parkingId)).orElse(BigDecimal.ZERO),
                Optional.ofNullable(vehicleHistoryRepository.findWeeklyEarnings(parkingId)).orElse(BigDecimal.ZERO),
                Optional.ofNullable(vehicleHistoryRepository.findMonthlyEarnings(parkingId)).orElse(BigDecimal.ZERO),
                Optional.ofNullable(vehicleHistoryRepository.findYearlyEarnings(parkingId)).orElse(BigDecimal.ZERO)
        );
    }

    public List<SocioEarningsDTO> getTop3SociosByWeeklyEarnings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        List<Object[]> results = vehicleHistoryRepository.findTop3SociosByWeeklyEarnings(startOfWeek, endOfWeek);
        return results.stream()
                .map(result -> new SocioEarningsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        (BigDecimal) result[2]
                ))
                .toList();
    }

    public List<ParkingTopEarningsDTO> getTop3ParkingsByWeeklyEarnings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        List<Object[]> results = vehicleHistoryRepository.findTop3ParkingsByWeeklyEarnings(startOfWeek, endOfWeek);
        return results.stream()
                .map(result -> new ParkingTopEarningsDTO(
                        (String) result[0],
                        (BigDecimal) result[1]
                ))
                .toList();
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getPlaca(),
                vehicle.getFechaIngreso(),
                vehicle.getParqueadero().getNombre()
        );
    }

}

