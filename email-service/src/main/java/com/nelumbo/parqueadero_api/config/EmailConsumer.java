package com.nelumbo.parqueadero_api.config;

import com.nelumbo.parqueadero_api.dto.BulletinEmailDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.logging.Logger;

@Component
public class EmailConsumer {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @RabbitListener(queues = RabbitMQConfig.QUEUE, concurrency = "2")
    public void processBulletin(BulletinEmailDTO emailData) throws InterruptedException {

        if (emailData.getEmail().equals("juan2@example.com")) {
            throw new RuntimeException("Error simulado en el consumidor para " + emailData.getEmail());
        }
        logger.info("Procesando correo a: " + emailData.getEmail());

        Thread.sleep(new Random().nextInt(4000) + 1000); // Simula envío entre 1 y 5 segundos

        logger.info("Correo enviado exitosamente a: " + emailData.getEmail());
    }

}
