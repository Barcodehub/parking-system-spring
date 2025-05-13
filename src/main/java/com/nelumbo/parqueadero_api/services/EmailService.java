package com.nelumbo.parqueadero_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Simula el envío de un email
     * @param toEmail Dirección de email del destinatario
     * @param subject Asunto del email
     * @param body Cuerpo del mensaje
     */
    public void sendEmail(String toEmail, String subject, String body) {
        // En una implementación real, aquí se conectaría con un servicio de email
        logger.info("Simulando envío de email a: {}", toEmail);
        logger.info("Asunto: {}", subject);
        logger.info("Cuerpo del mensaje:\n{}", body);

        // Simulamos un pequeño retardo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("Email simulado enviado con éxito a {}", toEmail);
    }
}