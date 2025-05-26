package com.nelumbo.parqueadero_api.integration;

import com.nelumbo.parqueadero_api.exception.AuthenticationFailedException;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParkingRepository parkingRepository;


    void setup() {
        // Crear un usuario de prueba
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("tesUser");
        testUser.setPassword(passwordEncoder.encode("PASSword123!!"));
        testUser.setRole(Role.SOCIO);
        userRepository.save(testUser);
    }

    @BeforeEach
    void cleanUp() {
        parkingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        setup();

        String requestBody = """
        {
            "email": "test@example.com",
            "password": "PASSword123!!"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void login_WhenInvalidCredentials_ShouldReturnError() throws Exception {
        // Arrange
        when(authService.authenticate(any()))
                .thenThrow(new AuthenticationFailedException("Credenciales inválidas", "password"));

        String requestBody = """
        {
            "email": "user@example.com",
            "password": "wrongPassword1!!"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errors[0].description").value("Usuario NO encontrado para ese E-mail"))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void login_WhenInvalidEmailFormat_ShouldReturnValidationError() throws Exception {
        // Arrange (no need to mock service as validation happens before)
        String requestBody = """
        {
            "email": "invalid-email",
            "password": "PAssword12!"
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("Formato de correo inválido"))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }
}