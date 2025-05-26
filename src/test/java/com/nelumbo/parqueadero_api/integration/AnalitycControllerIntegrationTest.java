package com.nelumbo.parqueadero_api.integration;


import com.nelumbo.parqueadero_api.models.*;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.VehicleHistoryRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nelumbo.parqueadero_api.dto.errors.ResponseMessages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_ADMIN")
class AnalitycControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleHistoryRepository vehicleHistory;

    private User socio;
    private Integer parkingId;

    @BeforeEach
    void setUp() {

        vehicleRepository.deleteAll();
        parkingRepository.deleteAll();
        userRepository.deleteAll();

        socio = new User();
        socio.setName("Socio Test");
        socio.setEmail("socio@test.com");
        socio.setPassword("password");
        socio.setRole(Role.SOCIO);
        userRepository.save(socio);

        Parking parking = new Parking();
        parking.setNombre("Parqueadero Test");
        parking.setCapacidad(100);
        parking.setCostoPorHora(BigDecimal.valueOf(10));
        parking.setSocio(socio);
        parkingRepository.save(parking);

        parkingId = parking.getId();

        Vehicle vehicle = new Vehicle();
        vehicle.setPlaca("QQQ123");
        vehicle.setParqueadero(parking);
        vehicle.setFechaIngreso(LocalDateTime.now());
        vehicle.setSocio(socio);
        vehicleRepository.save(vehicle);

    }

    @Test
    void shouldReturnTop10MostFrequentVehicles() throws Exception {
        mockMvc.perform(get("/api/analityc/vehicles/top-global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value(No_FREQ_PARKING));

    }
    @Test
    void shouldReturnTop10VehiclesByParking() throws Exception {

        mockMvc.perform(get("/api/analityc/parkings/{parkingId}/vehicles/top", parkingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value(No_FREQ_IN_PARKING));
    }
    @Test
    void shouldReturnFirstTimeVehicles() throws Exception {
        mockMvc.perform(get("/api/analityc/parkings/{parkingId}/vehicles/first-time", parkingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldReturnFirstTimeVehiclesEmpty() throws Exception {
        vehicleRepository.deleteAll();
        mockMvc.perform(get("/api/analityc/parkings/{parkingId}/vehicles/first-time", parkingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value(No_VEH_FIRST_TIME));
    }

    @Test
    void shouldReturnParkingEarnings() throws Exception {
        mockMvc.perform(get("/api/analityc/parkings/{parkingId}/earnings", parkingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value(No_INGRESOS));
    }

    @Test
    void shouldReturnTop3SociosByWeeklyEarnings() throws Exception {
        mockMvc.perform(get("/api/analityc/socios/top-earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value(No_WEEK_INGRESOS));
    }
    @Test
    void shouldReturnTop3ParkingsByWeeklyEarnings() throws Exception {
        mockMvc.perform(get("/api/analityc/parkings/top-earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value(No_WEEK_ParkingINGRESOS));
    }


}

