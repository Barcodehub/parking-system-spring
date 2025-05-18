package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.EmailRequest;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
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

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<Map<String, String>>> sendEmailToSocios(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EmailRequest emailRequest) {

        return ResponseEntity.ok(emailService.handleEmailSending(userDetails, emailRequest));
    }
}