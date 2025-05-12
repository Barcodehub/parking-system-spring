package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.models.Role;

public record AuthResponseDTO(
        String token,
        String email,
        Role role
) {}