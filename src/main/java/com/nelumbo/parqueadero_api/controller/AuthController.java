package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.AuthRequestDTO;
import com.nelumbo.parqueadero_api.dto.AuthResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody AuthRequestDTO request, @RequestHeader("X-Device-Id") String deviceId) {
        return authService.authenticate(request, deviceId);
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponseDTO<String>> logout(
            HttpServletRequest request,
            @RequestHeader("X-Device-Id") String deviceId) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : null;

        return authService.logout(token, deviceId);
    }
}