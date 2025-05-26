package com.nelumbo.parqueadero_api.integration;

import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_ADMIN")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParkingRepository parkingRepository;

    @BeforeEach
    void cleanUp() {
        parkingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createUser_WhenValidRequest_ShouldReturn201() throws Exception {
        String requestBody = """
        {
            "name": "John Doe",
            "email": "john0@example.com",
            "password": "PASSword123!"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Verifica en la base de datos
        assertTrue(userRepository.findByEmail("john0@example.com").isPresent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createUser_WhenEmailExists_ShouldReturnError() throws Exception {
        // Arrange
        User existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("PASSword123!");
        existingUser.setRole(Role.SOCIO);
        userRepository.save(existingUser);

        String requestBody = """
        {
            "name": "John Doe",
            "email": "existing@example.com",
            "password": "PASSword123!"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].description").value("El email ya está registrado"))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }



        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        void createUser_WhenNameIsBlank_ShouldReturn400() throws Exception {
            String requestBody = """
        {
            "name": "",
            "email": "valid@example.com",
            "password": "validPassword123!"
        }
        """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value("name"));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        void createUser_WhenEmailInvalid_ShouldReturn400() throws Exception {
            String requestBody = """
        {
            "name": "Valid Name",
            "email": "invalid-email",
            "password": "validPassword123!"
        }
        """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value("email"));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        void createUser_WhenPasswordTooShort_ShouldReturn400() throws Exception {
            String requestBody = """
        {
            "name": "Valid Name",
            "email": "valid@example.com",
            "password": "123"
        }
        """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value("password"));
        }

}