package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.services.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> sendEmailToSocios(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EmailRequest emailRequest) {

        // Verificar que el usuario actual es ADMIN
        User currentUser = (User) userDetails;
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo los administradores pueden enviar emails a los socios");
        }

        if (emailRequest.socioId() != null) {
            // Enviar a un socio específico
            User socio = userRepository.findById(emailRequest.socioId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Socio no encontrado"));

            if (socio.getRole() != Role.SOCIO) {
                return ResponseEntity.badRequest()
                        .body("El usuario especificado no es un socio");
            }

            emailService.sendEmail(
                    socio.getEmail(),
                    emailRequest.subject(),
                    emailRequest.message());

            return ResponseEntity.ok(
                    Map.of("message", "Email enviado al socio " + socio.getName()));
        } else {
            // Enviar a todos los socios
            List<User> socios = userRepository.findByRole(Role.SOCIO);

            socios.forEach(socio -> emailService.sendEmail(
                    socio.getEmail(),
                    emailRequest.subject(),
                    emailRequest.message()));

            return ResponseEntity.ok(
                    Map.of("message", "Email enviado a " + socios.size() + " socios"));
        }
    }
}