package com.nelumbo.parqueadero_api.integration;

import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_ADMIN")
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Integer parkingId = null;
    @BeforeEach
    void setUp() {

        parkingRepository.deleteAll();
        userRepository.deleteAll();
        vehicleRepository.deleteAll();

        // Crear usuario en la base de datos
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        user.setRole(Role.SOCIO);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        // Crear parqueadero
        Parking parking = new Parking();
        parking.setNombre("ParkingOne");
        parking.setCapacidad(50);
        parking.setSocio(user);
        parking.setCostoPorHora(new BigDecimal("10.00"));
        parking = parkingRepository.save(parking);
        parkingId = parking.getId();
    }


    @Test
    @WithMockUser(username="user@example.com", authorities = "ROLE_SOCIO") // Spring Security cargará este usuario
    void registerVehicleEntry_ShouldReturn201AndSaveVehicle() throws Exception {
        String requestJson = String.format("""
        {
          "placa": "DDD999",
          "parqueaderoId": %d
        }
    """, parkingId);

        mockMvc.perform(post("/api/vehicles/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.Id").exists());

        // Validar que el vehículo fue guardado
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByPlacaAndFechaSalidaIsNull("DDD999");
        assertTrue(vehicleOpt.isPresent());
    }


    @Test
    @WithMockUser(username="user@example.com", authorities = "ROLE_SOCIO")
    void registerVehicleExit_ShouldReturn200AndDeleteVehicle() throws Exception {
        // Preinsertar vehículo activo
        User user = userRepository.findByEmail("user@example.com").orElseThrow();
        Parking parking = parkingRepository.findById(parkingId).orElseThrow();

        Vehicle vehicle = Vehicle.builder()
                .placa("DDD999")
                .fechaIngreso(LocalDateTime.now().minusHours(2))
                .socio(user)
                .parqueadero(parking)
                .build();
        vehicleRepository.save(vehicle);

        String requestJson = String.format("""
        {
          "placa": "DDD999",
          "parqueaderoId": %d
        }
    """, parkingId);

        mockMvc.perform(post("/api/vehicles/exit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mensaje").value("Salida registrada"));

        // Validar que el vehículo ya no existe
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByPlacaAndFechaSalidaIsNull("DDD999");
        assertFalse(vehicleOpt.isPresent());
    }

}

