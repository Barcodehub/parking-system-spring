package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.validation.annotations.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email formato no valido")
        String email,

        @NotBlank(message = "Password is required")
        @StrongPassword
        String password

) {}