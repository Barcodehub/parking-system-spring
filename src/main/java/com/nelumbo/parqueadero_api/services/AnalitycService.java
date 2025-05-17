package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.*;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
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
    private final ParkingRepository parkingRepository;

    public SuccessResponseDTO<List<VehicleFrequencyDTO>> getTop10MostFrequentVehicles() {
        List<Object[]> results = vehicleHistoryRepository.findTop10MostFrequentVehicles();

        List<VehicleFrequencyDTO> responseList = results.stream()
                .map(result -> new VehicleFrequencyDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .toList();

        // Si la lista está vacía, devolvemos data vacía (no es un error)
        //return new SuccessResponseDTO<>(responseList);

        // O si prefieres considerar lista vacía como error:
         if (responseList.isEmpty()) {
             throw new ResourceNotFoundException("No hay vehículos frecuentes o no existen parqueaderos");
         }
         return new SuccessResponseDTO<>(responseList);
    }

    public SuccessResponseDTO<List<VehicleFrequencyDTO>> getTop10MostFrequentVehiclesByParking(Long parkingId) {
        List<Object[]> results = vehicleHistoryRepository.findTop10ByParkingId(parkingId);

        List<VehicleFrequencyDTO> vehicles = results.stream()
                .map(result -> new VehicleFrequencyDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .toList();

        // Si quieres manejar lista vacía como caso válido (recomendado)
       // return new SuccessResponseDTO<>(vehicles);

        // O si prefieres mantenerlo como error (como en tu código original)

    if (vehicles.isEmpty()) {
        throw new ResourceNotFoundException(
                String.format("No hay vehículos frecuentes en el parqueadero %d", parkingId));
    }
    return new SuccessResponseDTO<>(vehicles);

    }

    public SuccessResponseDTO<List<VehicleDTO>> getFirstTimeVehicles(Long parkingId) {
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(Math.toIntExact(parkingId));

        if (!parkingRepository.existsById(Math.toIntExact(parkingId))) {
            throw new ResourceNotFoundException("El parqueadero especificado no existe");
        }
        if (vehicles.isEmpty()) {
            throw new ResourceNotFoundException("Actualmente no hay vehículos en este parqueadero");
        }

        List<String> existingVehicles = vehicleHistoryRepository.findPlacasByParking(parkingId);
        List<VehicleDTO> firstTimeVehicles = vehicles.stream()
                .filter(v -> !existingVehicles.contains(v.getPlaca()))
                .map(this::convertToDTO)
                .toList();

        if (firstTimeVehicles.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("No hay vehículos por primera vez en el parqueadero %d", parkingId));
        }

        return new SuccessResponseDTO<>(firstTimeVehicles);
    }

    public SuccessResponseDTO<ParkingEarningsDTO> getParkingEarnings(Long parkingId) {

        if (!parkingRepository.existsById(Math.toIntExact(parkingId))) {
            throw new ResourceNotFoundException("El parqueadero especificado no existe");
        }

        BigDecimal today = vehicleHistoryRepository.findTodayEarnings(parkingId);
        BigDecimal weekly = vehicleHistoryRepository.findWeeklyEarnings(parkingId);
        BigDecimal monthly = vehicleHistoryRepository.findMonthlyEarnings(parkingId);
        BigDecimal yearly = vehicleHistoryRepository.findYearlyEarnings(parkingId);

        if (today == null && weekly == null && monthly == null && yearly == null) {
            throw new ResourceNotFoundException(
                    String.format("No se encontraron registros de ingresos para el parqueadero %d", parkingId));
        }

        ParkingEarningsDTO earningsDTO = new ParkingEarningsDTO(
                Optional.ofNullable(today).orElse(BigDecimal.ZERO),
                Optional.ofNullable(weekly).orElse(BigDecimal.ZERO),
                Optional.ofNullable(monthly).orElse(BigDecimal.ZERO),
                Optional.ofNullable(yearly).orElse(BigDecimal.ZERO)
        );

        return new SuccessResponseDTO<>(earningsDTO);
    }

    public SuccessResponseDTO<List<SocioEarningsDTO>> getTop3SociosByWeeklyEarnings() {        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        List<Object[]> results = vehicleHistoryRepository.findTop3SociosByWeeklyEarnings(startOfWeek, endOfWeek);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("no hay datos de ingresos semanales para socios o no hay Socios registrados en el sistema ");
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
            throw new ResourceNotFoundException("No hay datos de ingresos semanales para parqueaderos o no hay parqueaderos registrados");
        }

        List<ParkingTopEarningsDTO> topParkings = results.stream()
                .map(result -> new ParkingTopEarningsDTO(
                        (String) result[0],
                        (BigDecimal) result[1]
                ))
                .toList();

        return new SuccessResponseDTO<>(topParkings);
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

