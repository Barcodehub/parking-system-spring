package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @Email(message = "Formato de correo inválido")
        @NotBlank(message = "El campo email no puede estar vacío")
        String email,
        @NotBlank(message = "El campo contraseña no puede estar vacío")
        String password
) {}