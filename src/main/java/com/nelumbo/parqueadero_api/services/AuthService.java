package com.nelumbo.parqueadero_api.services;


import com.nelumbo.parqueadero_api.dto.AuthRequestDTO;
import com.nelumbo.parqueadero_api.dto.AuthResponseDTO;
import com.nelumbo.parqueadero_api.exception.AuthenticationFailedException;
import com.nelumbo.parqueadero_api.exception.HandleInternalServerError;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.security.JwtService;
import jakarta.validation.Valid;
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

    public AuthResponseDTO authenticate(@Valid AuthRequestDTO request) {
        try {
            // Primero obtenemos el usuario
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new AuthenticationFailedException("Usuario NO encontrado para ese E-mail"));

            // Autenticación con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // Generación del token JWT
            String jwtToken = jwtService.generateToken(user);
           // System.out.println("Token generado para " + user.getEmail() + ": " + jwtToken);

            return new AuthResponseDTO(
                    jwtToken,
                    user.getEmail(),
                    user.getRole()
            );

        } catch (AuthenticationFailedException e) {
            // Esto capturará específicamente cuando el usuario no existe
            throw new AuthenticationFailedException(e.getMessage());
        } catch (BadCredentialsException e) {
            // Esto capturará cuando la contraseña es incorrecta
            throw new AuthenticationFailedException("Contraseña incorrecta");
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandleInternalServerError("Authentication failed: " + e.getMessage());
        }
    }
}
