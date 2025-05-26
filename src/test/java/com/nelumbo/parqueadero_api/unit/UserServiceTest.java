package com.nelumbo.parqueadero_api.unit;


import com.nelumbo.parqueadero_api.dto.UserRequestDTO;
import com.nelumbo.parqueadero_api.dto.UserResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.EmailAlreadyExistsException;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_WhenValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("John Doe", "john@example.com", "password123");
        User savedUser = new User(1, "John Doe", "john@example.com", "encodedPassword", Role.SOCIO, LocalDateTime.now(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        SuccessResponseDTO<UserResponseDTO> response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        assertNull(response.errors());
        assertEquals(1, response.data().id());
        assertEquals("John Doe", response.data().name());
        assertEquals("john@example.com", response.data().email());
        assertEquals(Role.SOCIO, response.data().role());

        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("John Doe", "existing@example.com", "password123");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(request));

        assertEquals("El email ya está registrado", exception.getMessage());
        assertEquals("email", exception.getFieldName());

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WhenValidRequest_ShouldReturnCorrectMappedResponse() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("John Doe", "john@example.com", "password123");
        User savedUser = new User(1, "John Doe", "john@example.com", "encodedPassword", Role.SOCIO, LocalDateTime.now(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        SuccessResponseDTO<UserResponseDTO> response = userService.createUser(request);

        // Assert - Verificamos que el mapeo se hizo correctamente
        assertEquals(1, response.data().id());
        assertEquals("John Doe", response.data().name());
        assertEquals("john@example.com", response.data().email());
        assertEquals(Role.SOCIO, response.data().role());
    }

}