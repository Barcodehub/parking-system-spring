package com.nelumbo.parqueadero_api.dto;

public record AuthRequestDTO(
        String email,
        String password
) {}