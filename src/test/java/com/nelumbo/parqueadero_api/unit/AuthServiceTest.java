package com.nelumbo.parqueadero_api.unit;

import com.nelumbo.parqueadero_api.dto.AuthRequestDTO;
import com.nelumbo.parqueadero_api.dto.AuthResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.AuthenticationFailedException;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.security.JwtService;
import com.nelumbo.parqueadero_api.services.AuthService;
import com.nelumbo.parqueadero_api.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private SessionService sessionService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_WhenValidCredentials_ShouldReturnToken() {
        // Arrange

        String deviceId = "device-123";
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "password");
        User mockUser = new User(1, "Test User", "user@example.com", "encodedPass", Role.SOCIO, LocalDateTime.now(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser, deviceId)).thenReturn("mock.jwt.token");

        // Mock successful authentication
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        // Act
        ResponseEntity<SuccessResponseDTO<AuthResponseDTO>> response =
                authService.authenticate(request, deviceId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AuthResponseDTO responseBody = Objects.requireNonNull(response.getBody()).data();
        assertEquals("mock.jwt.token", responseBody.token());
        assertEquals("user@example.com", responseBody.email());
        assertEquals(Role.SOCIO, responseBody.role());

        verify(userRepository).findByEmail("user@example.com");
        verify(jwtService).generateToken(mockUser, deviceId);
        verify(authenticationManager).authenticate(any());
        verify(sessionService).createSession("user@example.com", deviceId, "mock.jwt.token");
    }

    @Test
    void authenticate_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        String deviceId = "device-123";
        AuthRequestDTO request = new AuthRequestDTO("nonexistent@example.com", "password");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.authenticate(request, deviceId)
        );

        assertEquals("Usuario NO encontrado para ese E-mail", exception.getMessage());
        assertEquals("email", exception.getField());

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void authenticate_WhenBadCredentials_ShouldThrowException() {
        // Arrange
        String deviceId = "device-123";
        AuthRequestDTO request = new AuthRequestDTO("user@example.com", "wrongpass");
        User mockUser = new User(1, "Test User", "user@example.com", "encodedPass", Role.SOCIO, LocalDateTime.now(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.authenticate(request, deviceId)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());
        assertEquals("password", exception.getField());

        verify(userRepository).findByEmail("user@example.com");
        verify(authenticationManager).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }
}