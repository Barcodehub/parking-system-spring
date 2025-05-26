package com.nelumbo.parqueadero_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ParkingRepository parkingRepository;

    private String adminEmail = "admin@test.com";
    private String socioEmail = "socio1@test.com";

    private User adminUser;
    private User socioUser;

    @BeforeEach
    void setUp() {
        parkingRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword("password");
        adminUser.setName("Admin");
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        socioUser = new User();
        socioUser.setEmail(socioEmail);
        socioUser.setPassword("password");
        socioUser.setName("Socio 1");
        socioUser.setRole(Role.SOCIO);
        userRepository.save(socioUser);
    }

    @BeforeEach
    void setupAuth() {
        // Suponiendo que adminUser ya fue guardado en userRepository
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                adminUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    void userAuth() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                socioUser, null, List.of(new SimpleGrantedAuthority("ROLE_SOCIO")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void shouldSendEmailToAllSocios() throws Exception {

        EmailRequest request = new EmailRequest("Aviso", "Mensaje para todos", null);

        mockMvc.perform(post("/api/admin/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value(Matchers.containsString("Email enviado a")));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void shouldSendEmailToSingleSocio() throws Exception {

        EmailRequest request = new EmailRequest("Privado", "Solo para ti", socioUser.getId());

        mockMvc.perform(post("/api/admin/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value(Matchers.containsString("Email enviado al socio")));
    }

    @Test
    @WithMockUser(username = "socio1@test.com", authorities = "ROLE_SOCIO")
    void shouldReturn403ForNonAdminUser() throws Exception {
        userAuth();
        EmailRequest request = new EmailRequest("No permitido", "Mensaje", null);

        mockMvc.perform(post("/api/admin/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void shouldReturn404WhenSocioNotFound() throws Exception {

        EmailRequest request = new EmailRequest("Alerta", "Mensaje", 9999); // socio inexistente

        mockMvc.perform(post("/api/admin/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].description").value("Socio no encontrado con ID: 9999"))
                .andExpect(jsonPath("$.errors[0].field").value("socioId"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void shouldReturn400WhenTargetUserIsNotSocio() throws Exception {

        User noSocio = new User();
        noSocio.setEmail("cliente@test.com");
        noSocio.setName("Cliente");
        noSocio.setPassword("123");
        noSocio.setRole(Role.ADMIN); // no es SOCIO
        userRepository.save(noSocio);

        EmailRequest request = new EmailRequest("Error", "Mensaje", noSocio.getId());

        mockMvc.perform(post("/api/admin/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("El usuario especificado no es un socio"));
    }
}
