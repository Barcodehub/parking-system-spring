package com.nelumbo.parqueadero_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nelumbo.parqueadero_api.dto.*;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.models.Parking;

import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.services.AnalitycService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalitycServiceTest {

    @Mock
    private VehicleHistoryRepository vehicleHistoryRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private AnalitycService analitycService;

    @Test
    void getTop10MostFrequentVehicles_ShouldReturnList() {
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{"ABC123", 5L});
        when(vehicleHistoryRepository.findTop10MostFrequentVehicles()).thenReturn(mockResult);

        SuccessResponseDTO<List<VehicleFrequencyDTO>> response = analitycService.getTop10MostFrequentVehicles();

        assertNotNull(response.data());
        assertEquals("ABC123", response.data().get(0).placa());
        assertEquals(5L, response.data().get(0).count());
    }

    @Test
    void getTop10MostFrequentVehiclesByParking_ShouldReturnList() {
        Integer parkingId = 1;
        when(parkingRepository.findById(parkingId)).thenReturn(Optional.of(new Parking()));
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{"XYZ789", 3L});
        when(vehicleHistoryRepository.findTop10ByParkingId(1L)).thenReturn(mockResult);

        SuccessResponseDTO<List<VehicleFrequencyDTO>> response = analitycService.getTop10MostFrequentVehiclesByParking(parkingId);

        assertNotNull(response.data());
        assertEquals("XYZ789", response.data().get(0).placa());
    }

    @Test
    void getFirstTimeVehicles_ShouldReturnFilteredVehicles() {
        Integer parkingId = 1;
        Vehicle vehicle = new Vehicle();
        vehicle.setPlaca("ABC123");
        vehicle.setFechaIngreso(LocalDateTime.now());

        Parking mockParking = new Parking();
        mockParking.setNombre("Central");
        vehicle.setParqueadero(mockParking);

        when(parkingRepository.findById(parkingId)).thenReturn(Optional.of(new Parking()));
        when(vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId)).thenReturn(List.of(vehicle));
        when(vehicleHistoryRepository.findPlacasByParking(1L)).thenReturn(List.of("XYZ789")); // no contiene "ABC123"

        SuccessResponseDTO<List<VehicleDTO>> response = analitycService.getFirstTimeVehicles(parkingId);

        assertNotNull(response.data());
        assertEquals("ABC123", response.data().get(0).placa());
    }

    @Test
    void getParkingEarnings_ShouldReturnEarningsDTO() {
        Integer parkingId = 1;
        when(parkingRepository.findById(parkingId)).thenReturn(Optional.of(new Parking()));
        when(vehicleHistoryRepository.findTodayEarnings(1L)).thenReturn(BigDecimal.valueOf(50));
        when(vehicleHistoryRepository.findWeeklyEarnings(1L)).thenReturn(BigDecimal.valueOf(200));
        when(vehicleHistoryRepository.findMonthlyEarnings(1L)).thenReturn(BigDecimal.valueOf(800));
        when(vehicleHistoryRepository.findYearlyEarnings(1L)).thenReturn(BigDecimal.valueOf(5000));

        SuccessResponseDTO<ParkingEarningsDTO> response = analitycService.getParkingEarnings(parkingId);

        assertNotNull(response.data());
        assertEquals(BigDecimal.valueOf(200), response.data().week());
    }

    @Test
    void getTop3SociosByWeeklyEarnings_ShouldReturnList() {
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{"Juan", 10L, BigDecimal.valueOf(100)});

        when(vehicleHistoryRepository.findTop3SociosByWeeklyEarnings(any(), any())).thenReturn(mockResult);

        SuccessResponseDTO<List<SocioEarningsDTO>> response = analitycService.getTop3SociosByWeeklyEarnings();

        assertNotNull(response.data());
        assertEquals("Juan", response.data().get(0).socioName());
    }

    @Test
    void getTop3ParkingsByWeeklyEarnings_ShouldReturnList() {
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{"Central Parking",BigDecimal.valueOf(500)});
        when(vehicleHistoryRepository.findTop3ParkingsByWeeklyEarnings(any(), any())).thenReturn(mockResult);

        SuccessResponseDTO<List<ParkingTopEarningsDTO>> response = analitycService.getTop3ParkingsByWeeklyEarnings();

        assertNotNull(response.data());
        assertEquals("Central Parking", response.data().get(0).parkingName());
    }
}

