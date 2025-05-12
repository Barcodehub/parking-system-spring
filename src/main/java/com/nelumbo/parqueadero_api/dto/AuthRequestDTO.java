package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        String email,
        String password
) {}