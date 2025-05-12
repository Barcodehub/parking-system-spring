package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.models.Role;
import java.time.LocalDateTime;

public record UserResponseDTO(
        Integer id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) {}