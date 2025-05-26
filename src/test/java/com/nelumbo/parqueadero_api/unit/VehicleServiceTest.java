package com.nelumbo.parqueadero_api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleEntryResponseDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitRequestDTO;
import com.nelumbo.parqueadero_api.dto.VehicleExitResultDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.BusinessRuleException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.models.VehicleHistory;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.services.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleHistoryRepository historyRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VehicleService vehicleService;


    @BeforeEach
    void setUp() {
        // Configura un usuario falso autenticado
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user@example.com")
                .password("password")
                .authorities(new ArrayList<>()) // o cualquier rol necesario
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void registerVehicleEntry_ShouldRegisterEntrySuccessfully() {
        // Arrange
        VehicleEntryRequestDTO request = new VehicleEntryRequestDTO("ABC123", 1);
        String userEmail = "user@example.com";

        User mockUser = new User();
        mockUser.setEmail(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

        Parking mockParking = new Parking();
        mockParking.setId(1);
        mockParking.setCostoPorHora(new BigDecimal("10.00"));

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(100);
        savedVehicle.setPlaca("ABC123");
        savedVehicle.setParqueadero(mockParking);
        savedVehicle.setSocio(mockUser);
        savedVehicle.setFechaIngreso(LocalDateTime.now());

        when(parkingRepository.findById(1)).thenReturn(Optional.of(mockParking));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        // Act
        SuccessResponseDTO<VehicleEntryResponseDTO> response =
                vehicleService.registerVehicleEntry(request, userEmail);

        // Assert
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(100, response.data().Id());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    void registerVehicleEntry_ShouldThrowExceptionWhenParkingNotFound() {
        // Arrange
        VehicleEntryRequestDTO request = new VehicleEntryRequestDTO("ABC999", 1);
        String userEmail = "user@example.com";

        when(parkingRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            vehicleService.registerVehicleEntry(request, userEmail);
        });
    }


    @Test
    void registerVehicleExit_ShouldRegisterExitSuccessfully() {
        // Arrange
        VehicleExitRequestDTO request = new VehicleExitRequestDTO("ABC123", 1);

        Parking mockParking = new Parking();
        mockParking.setId(1);
        mockParking.setCostoPorHora(new BigDecimal("10.00"));

        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(100);
        mockVehicle.setPlaca("ABC123");
        mockVehicle.setParqueadero(mockParking);
        mockVehicle.setFechaIngreso(LocalDateTime.now().minusHours(2));

        when(vehicleRepository.findByPlacaAndFechaSalidaIsNull("ABC123"))
                .thenReturn(Optional.of(mockVehicle));
        when(historyRepository.save(any(VehicleHistory.class))).thenReturn(new VehicleHistory());

        // Act
        SuccessResponseDTO<VehicleExitResultDTO> response =
                vehicleService.registerVehicleExit(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals("Salida registrada", response.data().mensaje());
        verify(vehicleRepository, times(1)).delete(mockVehicle);
        verify(historyRepository, times(1)).save(any(VehicleHistory.class));
    }

    @Test
    void registerVehicleExit_ShouldThrowExceptionWhenVehicleNotFound() {
        // Arrange
        VehicleExitRequestDTO request = new VehicleExitRequestDTO("ABC123", 1);

        when(vehicleRepository.findByPlacaAndFechaSalidaIsNull("ABC123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            vehicleService.registerVehicleExit(request);
        });
    }

}