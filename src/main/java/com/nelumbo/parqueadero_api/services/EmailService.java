package com.nelumbo.parqueadero_api.services;


import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.FieldAwareResponseStatusException;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final UserRepository userRepository;

    public SuccessResponseDTO<Map<String, String>> handleEmailSending(UserDetails userDetails, EmailRequest emailRequest) {
        // Verificar que el usuario actual es ADMIN
        User currentUser = (User) userDetails;
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo los administradores pueden enviar emails a los socios");
        }


        if (emailRequest.socioId() != null) {
            return new SuccessResponseDTO<>(sendEmailToSingleSocio(emailRequest));
        } else {
            return new SuccessResponseDTO<>(sendEmailToAllSocios(emailRequest));
        }
    }

    private Map<String, String> sendEmailToSingleSocio(EmailRequest emailRequest) {
        User socio = userRepository.findById(emailRequest.socioId())
                .orElseThrow(() -> new FieldAwareResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Socio no encontrado con ID: " + emailRequest.socioId(),
                        "socioId"));

        if (socio.getRole() != Role.SOCIO) {
            throw new IllegalArgumentException("El usuario especificado no es un socio" );
        }

        sendEmail(socio.getEmail(), emailRequest.subject(), emailRequest.message());

        return Map.of("message", "Email enviado al socio " + socio.getName());
    }

    private Map<String, String> sendEmailToAllSocios(EmailRequest emailRequest) {
        List<User> socios = userRepository.findByRole(Role.SOCIO);

        socios.forEach(socio -> sendEmail(
                socio.getEmail(),
                emailRequest.subject(),
                emailRequest.message()));

        return Map.of("message", "Email enviado a " + socios.size() + " socios");
    }

    public void sendEmail(String toEmail, String subject, String body) {
        logger.info("Simulando envío de email a: {}", toEmail);
        logger.info("Asunto: {}", subject);
        logger.info("Cuerpo del mensaje:\n{}", body);

        // Simula un pequeño retardo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("Email simulado enviado con éxito a {}", toEmail);
    }
}