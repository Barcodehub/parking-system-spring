package com.nelumbo.parqueadero_api.services;


import com.nelumbo.parqueadero_api.dto.AuthRequestDTO;
import com.nelumbo.parqueadero_api.dto.AuthResponseDTO;
import com.nelumbo.parqueadero_api.exception.AuthenticationFailedException;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        try {
            // Primero obtenemos el usuario
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new AuthenticationFailedException("User not found"));

            // Verificación manual de contraseña con logging
            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                System.out.println("DEBUG - Password mismatch:");
                System.out.println("Provided: " + request.password());
                System.out.println("Stored: " + user.getPassword());
                System.out.println("Encoded provided: " + passwordEncoder.encode(request.password()));
                throw new AuthenticationFailedException("Invalid credentials");
            }

            // Autenticación con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // Generación del token JWT
            String jwtToken = jwtService.generateToken(user);

            return new AuthResponseDTO(
                    jwtToken,
                    user.getEmail(),
                    user.getRole()
            );

        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid email or password");
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationFailedException("Authentication failed: " + e.getMessage());
        }
    }
}
