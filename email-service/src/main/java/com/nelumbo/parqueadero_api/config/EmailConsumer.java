package com.nelumbo.parqueadero_api.config;

import com.nelumbo.parqueadero_api.dto.BulletinEmailDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.logging.Logger;

@Component
public class EmailConsumer {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @RabbitListener(queues = RabbitMQConfig.QUEUE, concurrency = "2")
    public void processBulletin(BulletinEmailDTO emailData) throws InterruptedException {
        logger.info("Procesando correo a: " + emailData.getEmail());

        Thread.sleep(new Random().nextInt(4000) + 1000); // Simula envío entre 1 y 5 segundos

        logger.info("Correo enviado exitosamente a: " + emailData.getEmail());
    }
}
