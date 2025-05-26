package com.nelumbo.parqueadero_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@WithMockUser(authorities = "ROLE_ADMIN")
class ParkingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private UserRepository userRepository;

    private User socio;
    private User socioAdmin;


    @BeforeEach
    void setup() {

        parkingRepository.deleteAll();
        userRepository.deleteAll();

        socio = new User();
        socio.setName("Socio Test");
        socio.setEmail("socio@test.com");
        socio.setPassword("password");
        socio.setRole(Role.SOCIO);
        userRepository.save(socio);
    }


    @Test
    void createParking_ShouldReturnCreatedParking() throws Exception {
        ParkingRequestDTO request = new ParkingRequestDTO("Mi Parqueadero", 50, BigDecimal.valueOf(5.0), socio.getId());

        mockMvc.perform(post("/api/parkings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre").value("Mi Parqueadero"))
                .andExpect(jsonPath("$.data.capacidad").value(50));
    }

    @Test
    void createParking_ShouldReturnBusinness() throws Exception {

        parkingRepository.deleteAll();
        userRepository.deleteAll();

        socioAdmin = new User();
        socioAdmin.setName("Admin Test");
        socioAdmin.setEmail("admin@test.com");
        socioAdmin.setPassword("password");
        socioAdmin.setRole(Role.ADMIN);
        userRepository.save(socioAdmin);

        ParkingRequestDTO request = new ParkingRequestDTO("Mi Parqueadero", 50, BigDecimal.valueOf(5.0), socioAdmin.getId());

        mockMvc.perform(post("/api/parkings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].description").value("El usuario debe tener rol SOCIO"))
                .andExpect(jsonPath("$.errors[0].field").value("socioId"));
    }

    @Test
    void getParkingById_ShouldReturnParking() throws Exception {
        Parking parking = new Parking();
        parking.setNombre("Test Parking");
        parking.setCapacidad(30);
        parking.setCostoPorHora(BigDecimal.valueOf(3.0));
        parking.setSocio(socio);
        parkingRepository.save(parking);

        mockMvc.perform(get("/api/parkings/" + parking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Test Parking"))
                .andExpect(jsonPath("$.data.capacidad").value(30));
    }



    @Test
    void getAllParkings_AsAdmin_ShouldReturnAll() throws Exception {
        Parking parking1 = createParking("Parking 1", 20, BigDecimal.valueOf(2.5), socio);
        Parking parking2 = createParking("Parking 2", 25, BigDecimal.valueOf(3.0), socio);
        parkingRepository.saveAll(List.of(parking1, parking2));

        // Sin autenticación se simula como admin
        mockMvc.perform(get("/api/parkings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }
    private Parking createParking(String name, Integer capacidad, BigDecimal costo, User socioId){
        Parking parking = new Parking();
        parking.setNombre(name);
        parking.setCapacidad(capacidad);
        parking.setCostoPorHora(costo);
        parking.setSocio(socioId);
        return parkingRepository.save(parking);
    }
    @Test
    void updateParking_ShouldUpdateSuccessfully() throws Exception {
        Parking parking = createParking("Old Name", 40, BigDecimal.valueOf(4.0), socio);
        parkingRepository.save(parking);

        ParkingRequestDTO updated = new ParkingRequestDTO("Updated Name", 60, BigDecimal.valueOf(6.0), socio.getId());

        mockMvc.perform(put("/api/parkings/" + parking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Updated Name"))
                .andExpect(jsonPath("$.data.capacidad").value(60));
    }

    @Test
    void deleteParking_ShouldReturnOk() throws Exception {
        Parking parking =createParking("Delete Me", 10, BigDecimal.valueOf(1.0), socio);
        parkingRepository.save(parking);

        mockMvc.perform(delete("/api/parkings/" + parking.getId()))
                .andExpect(status().isOk());

        assertFalse(parkingRepository.findById(parking.getId()).isPresent());
    }

    @Test
    void getVehiclesInParking_AsAdmin_ShouldReturnEmptyListMessage() throws Exception {
        Parking parking =createParking("Empty Parking", 10, BigDecimal.valueOf(2.0), socio);
        parkingRepository.save(parking);

        mockMvc.perform(get("/api/parkings/parkings/" + parking.getId() + "/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errors").value("No hay vehiculos en este parqueadero"));
    }
}
