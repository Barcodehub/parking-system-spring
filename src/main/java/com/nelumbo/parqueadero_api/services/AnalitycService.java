package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.*;
import com.nelumbo.parqueadero_api.dto.errors.ResponseMessages;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class AnalitycService {

    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingRepository parkingRepository;

    public SuccessResponseDTO<List<VehicleFrequencyDTO>> getTop10MostFrequentVehicles() {
        List<Object[]> results = vehicleHistoryRepository.findTop10MostFrequentVehicles();

        List<VehicleFrequencyDTO> responseList = results.stream()
                .map(result -> new VehicleFrequencyDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .toList();
        return responseList.isEmpty()
                ? new SuccessResponseDTO<>(null, ResponseMessages.No_FREQ_PARKING)
                : new SuccessResponseDTO<>(responseList);
    }

    public SuccessResponseDTO<List<VehicleFrequencyDTO>> getTop10MostFrequentVehiclesByParking( Integer parkingId) {

        parkingExist(parkingId);
        List<Object[]> results = vehicleHistoryRepository.findTop10ByParkingId(Long.valueOf(parkingId));

        List<VehicleFrequencyDTO> vehicles = results.stream()
                .map(result -> new VehicleFrequencyDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .toList();

        return vehicles.isEmpty()
                ? new SuccessResponseDTO<>(null, ResponseMessages.No_FREQ_IN_PARKING)
                : new SuccessResponseDTO<>(vehicles);

    }



    public SuccessResponseDTO<List<VehicleDTO>> getFirstTimeVehicles(Integer  parkingId) {
        parkingExist(parkingId);
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(Math.toIntExact(parkingId));

        List<String> existingVehicles = vehicleHistoryRepository.findPlacasByParking(Long.valueOf(parkingId));
        List<VehicleDTO> firstTimeVehicles = vehicles.stream()
                .filter(v -> !existingVehicles.contains(v.getPlaca()))
                .map(this::convertToDTO)
                .toList();

        return firstTimeVehicles.isEmpty()
                    ? new SuccessResponseDTO<>(null, ResponseMessages.No_VEH_FIRST_TIME)
                : new SuccessResponseDTO<>(firstTimeVehicles);
    }

    public SuccessResponseDTO<ParkingEarningsDTO> getParkingEarnings(Integer parkingId) {

        parkingExist(parkingId);
        BigDecimal today = vehicleHistoryRepository.findTodayEarnings(Long.valueOf(parkingId));
        BigDecimal weekly = vehicleHistoryRepository.findWeeklyEarnings(Long.valueOf(parkingId));
        BigDecimal monthly = vehicleHistoryRepository.findMonthlyEarnings(Long.valueOf(parkingId));
        BigDecimal yearly = vehicleHistoryRepository.findYearlyEarnings(Long.valueOf(parkingId));

        ParkingEarningsDTO earningsDTO = new ParkingEarningsDTO(
                Optional.ofNullable(today).orElse(BigDecimal.ZERO),
                Optional.ofNullable(weekly).orElse(BigDecimal.ZERO),
                Optional.ofNullable(monthly).orElse(BigDecimal.ZERO),
                Optional.ofNullable(yearly).orElse(BigDecimal.ZERO)
        );

        if (today == null && weekly == null && monthly == null && yearly == null) {
            return new SuccessResponseDTO<>(null, ResponseMessages.No_INGRESOS);
        }
        return new SuccessResponseDTO<>(earningsDTO);
    }

    public SuccessResponseDTO<List<SocioEarningsDTO>> getTop3SociosByWeeklyEarnings() {        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        List<Object[]> results = vehicleHistoryRepository.findTop3SociosByWeeklyEarnings(startOfWeek, endOfWeek);

        if (results.isEmpty()) {
            return new SuccessResponseDTO<>(null, ResponseMessages.No_WEEK_INGRESOS);
        }

        List<SocioEarningsDTO> socios = results.stream()
                .map(result -> new SocioEarningsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        (BigDecimal) result[2]
                ))
                .toList();
        return new SuccessResponseDTO<>(socios);
    }

    public SuccessResponseDTO<List<ParkingTopEarningsDTO>> getTop3ParkingsByWeeklyEarnings() {        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        List<Object[]> results = vehicleHistoryRepository.findTop3ParkingsByWeeklyEarnings(startOfWeek, endOfWeek);

        if (results.isEmpty()) {
            return new SuccessResponseDTO<>(null, ResponseMessages.No_WEEK_ParkingINGRESOS);
        }

        List<ParkingTopEarningsDTO> topParkings = results.stream()
                .map(result -> new ParkingTopEarningsDTO(
                        (String) result[0],
                        (BigDecimal) result[1]
                ))
                .toList();

        return new SuccessResponseDTO<>(topParkings);
    }


    private Parking parkingExist(Integer id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));
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

