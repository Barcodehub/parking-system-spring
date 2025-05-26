package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "El asunto no puede estar vacío")
        String subject,

        @NotBlank(message = "El mensaje no puede estar vacío")
        String message,

        Integer socioId
) {}