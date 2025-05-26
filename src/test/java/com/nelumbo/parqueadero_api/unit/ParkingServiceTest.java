package com.nelumbo.parqueadero_api.unit;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.BusinessException;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.services.ParkingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private ParkingService parkingService;

    @Test
    void createParking_ShouldCreateParkingSuccessfully() {
        // Arrange
        ParkingRequestDTO request = new ParkingRequestDTO(
                "Parking Test",
                50,
                new BigDecimal("10.00"),
                1
        );

        User mockSocio = new User();
        mockSocio.setId(1);
        mockSocio.setRole(Role.SOCIO);

        Parking mockParking = new Parking();
        mockParking.setId(1);
        mockParking.setNombre("Parking Test");
        mockParking.setCapacidad(50);
        mockParking.setCostoPorHora(new BigDecimal("10.00"));
        mockParking.setSocio(mockSocio);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockSocio));
        when(parkingRepository.save(any(Parking.class))).thenReturn(mockParking);

        // Act
        SuccessResponseDTO<ParkingResponseDTO> response = parkingService.createParking(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(1, response.data().id());
        assertEquals("Parking Test", response.data().nombre());
        assertEquals(50, response.data().capacidad());
        assertEquals(new BigDecimal("10.00"), response.data().costoPorHora());
        assertEquals(1, response.data().socioId());
    }

    @Test
    void createParking_ShouldThrowBusinessException_WhenUserIsNotSocio() {
        // Arrange
        Integer userId = 1;
        User adminUser = new User();
        adminUser.setId(userId);
        adminUser.setName("Admin Test");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("password");
        adminUser.setRole(Role.ADMIN); // ❗️No es SOCIO

        ParkingRequestDTO request = new ParkingRequestDTO("Parqueadero X", 100, BigDecimal.valueOf(10), userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            parkingService.createParking(request);
        });

        assertEquals("El usuario debe tener rol SOCIO", exception.getMessage());
        assertEquals("socioId", exception.getField());
    }

    @Test
    void getParkingById_ShouldReturnParking() {
        // Arrange
        Parking mockParking = new Parking();
        mockParking.setId(1);
        mockParking.setNombre("Parking Test");
        mockParking.setCapacidad(50);
        mockParking.setCostoPorHora(new BigDecimal("10.00"));

        User mockSocio = new User();
        mockSocio.setId(1);
        mockParking.setSocio(mockSocio);

        when(parkingRepository.findById(1)).thenReturn(Optional.of(mockParking));

        // Act
        SuccessResponseDTO<ParkingResponseDTO> response = parkingService.getParkingById(1);

        // Assert
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(1, response.data().id());
    }

    @Test
    void getParkingById_ShouldThrowExceptionWhenInvalidId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            parkingService.getParkingById(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            parkingService.getParkingById(0);
        });
    }

    @Test
    void getParkingById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            parkingService.getParkingById(1);
        });
    }


    @Test
    void updateParking_ShouldUpdateParkingSuccessfully() {
        // Arrange
        ParkingRequestDTO request = new ParkingRequestDTO(
                "Updated Parking",
                100,
                new BigDecimal("15.00"),
                2
        );

        User mockSocio = new User();
        mockSocio.setId(2);
        mockSocio.setRole(Role.SOCIO);

        Parking existingParking = new Parking();
        existingParking.setId(1);
        existingParking.setNombre("Old Parking");
        existingParking.setCapacidad(50);
        existingParking.setCostoPorHora(new BigDecimal("10.00"));

        User oldSocio = new User();
        oldSocio.setId(1);
        existingParking.setSocio(oldSocio);

        when(parkingRepository.findById(1)).thenReturn(Optional.of(existingParking));
        when(userRepository.findById(2)).thenReturn(Optional.of(mockSocio));
        when(parkingRepository.save(any(Parking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SuccessResponseDTO<ParkingResponseDTO> response = parkingService.updateParking(1, request);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Parking", response.data().nombre());
        assertEquals(100, response.data().capacidad());
        assertEquals(new BigDecimal("15.00"), response.data().costoPorHora());
        assertEquals(2, response.data().socioId());
    }

    @Test
    void deleteParking_ShouldDeleteParking() {
        // Arrange
        Parking mockParking = new Parking();
        mockParking.setId(1);

        when(parkingRepository.findById(1)).thenReturn(Optional.of(mockParking));
        doNothing().when(parkingRepository).delete(mockParking);

        // Act
        SuccessResponseDTO<Void> response = parkingService.deleteParking(1);

        // Assert
        assertNotNull(response);
        assertNull(response.data());
        verify(parkingRepository, times(1)).delete(mockParking);
    }

    @Test
    void getVehiclesInParking_ShouldReturnVehiclesForAdmin() {
        // Arrange
        Integer parkingId = 1;
        Parking mockParking = createMockParking(parkingId, "Parking Test");

        Vehicle vehicle1 = createMockVehicle(1, "ABC123", mockParking);
        Vehicle vehicle2 = createMockVehicle(2, "XYZ789", mockParking);

        when(parkingRepository.findById(parkingId)).thenReturn(Optional.of(mockParking));
        when(vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId))
                .thenReturn(Arrays.asList(vehicle1, vehicle2));

        // Act - null userDetails simula acceso de admin
        SuccessResponseDTO<List<AdminVehicleResponseDTO>> response =
                parkingService.getVehiclesInParking(parkingId, null);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.data().size());
        verify(parkingRepository, times(1)).findById(parkingId);
        verify(vehicleRepository, times(1))
                .findByParqueaderoIdAndFechaSalidaIsNull(parkingId);
    }


    private Parking createMockParking(Integer id, String nombre) {
        Parking parking = new Parking();
        parking.setId(id);
        parking.setNombre(nombre);
        parking.setCapacidad(50);
        parking.setCostoPorHora(new BigDecimal("10.00"));

        User socio = new User();
        socio.setId(1);
        socio.setRole(Role.SOCIO);
        parking.setSocio(socio);

        return parking;
    }

    private Vehicle createMockVehicle(Integer id, String placa, Parking parking) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setPlaca(placa);
        vehicle.setFechaIngreso(LocalDateTime.now());
        vehicle.setParqueadero(parking);

        User socio = new User();
        socio.setId(1);
        socio.setName("Socio Test");
        vehicle.setSocio(socio);

        return vehicle;
    }
}