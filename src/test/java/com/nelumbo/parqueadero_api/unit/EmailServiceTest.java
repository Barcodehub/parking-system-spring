package com.nelumbo.parqueadero_api.unit;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.FieldAwareResponseStatusException;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailService emailService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1);
        adminUser.setRole(Role.ADMIN);
        adminUser.setEmail("admin@test.com");
        adminUser.setName("Admin Test");
    }

    @Test
    void handleEmailSending_shouldSendToAllSocios_whenSocioIdIsNull() {
        // Arrange
        EmailRequest request = new EmailRequest("Asunto", "Mensaje", null);

        User socio1 = new User();
        socio1.setEmail("socio1@test.com");
        socio1.setRole(Role.SOCIO);

        User socio2 = new User();
        socio2.setEmail("socio2@test.com");
        socio2.setRole(Role.SOCIO);

        when(userRepository.findByRole(Role.SOCIO)).thenReturn(List.of(socio1, socio2));

        // Act
        SuccessResponseDTO<Map<String, String>> response = emailService.handleEmailSending(adminUser, request);

        // Assert
        assertEquals("Email enviado a 2 socios", response.data().get("message"));
    }

    @Test
    void handleEmailSending_shouldSendToSingleSocio_whenValidSocioId() {
        // Arrange
        EmailRequest request = new EmailRequest("Asunto", "Mensaje", 99);

        User socio = new User();
        socio.setId(99);
        socio.setEmail("socio@test.com");
        socio.setName("Socio Test");
        socio.setRole(Role.SOCIO);

        when(userRepository.findById(99)).thenReturn(Optional.of(socio));

        // Act
        SuccessResponseDTO<Map<String, String>> response = emailService.handleEmailSending(adminUser, request);

        // Assert
        assertEquals("Email enviado al socio Socio Test", response.data().get("message"));
    }

    @Test
    void handleEmailSending_shouldThrowAccessDeniedException_whenUserIsNotAdmin() {
        // Arrange
        User socioUser = new User();
        socioUser.setRole(Role.SOCIO);

        EmailRequest request = new EmailRequest("Asunto", "Mensaje", null);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            emailService.handleEmailSending(socioUser, request);
        });
    }

    @Test
    void handleEmailSending_shouldThrowNotFound_whenSocioIdNotFound() {
        // Arrange
        EmailRequest request = new EmailRequest("Asunto", "Mensaje", 123);

        when(userRepository.findById(123)).thenReturn(Optional.empty());

        // Act & Assert
        FieldAwareResponseStatusException ex = assertThrows(FieldAwareResponseStatusException.class, () -> {
            emailService.handleEmailSending(adminUser, request);
        });

        assertEquals("socioId", ex.getField());
        assertEquals("Socio no encontrado con ID: 123", ex.getReason());
    }

    @Test
    void handleEmailSending_shouldThrowException_whenUserIsNotSocio() {
        // Arrange
        EmailRequest request = new EmailRequest("Asunto", "Mensaje", 999);

        User user = new User();
        user.setId(999);
        user.setRole(Role.ADMIN); // no es SOCIO

        when(userRepository.findById(999)).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            emailService.handleEmailSending(adminUser, request);
        });

        assertEquals("El usuario especificado no es un socio", ex.getMessage());
    }
}
