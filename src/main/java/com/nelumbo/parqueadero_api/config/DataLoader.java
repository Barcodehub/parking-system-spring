package com.nelumbo.parqueadero_api.config;

import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si ya existe un admin
            String adminEmail = "admin@mail.com";
            String adminPassword = "admin";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);


                userRepository.save(admin);
                System.out.println("Usuario admin creado: admin@mail.com / admin");

                System.out.println("Email: " + adminEmail);
                System.out.println("Password (plain): " + adminPassword);
                System.out.println("Password (encoded): " + admin.getPassword());
            }
        };
    }
}