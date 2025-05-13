package com.nelumbo.parqueadero_api.dto;

import java.time.LocalDateTime;

public record VehicleResponseDTO(
        Integer id,
        String placa,
        LocalDateTime createdAt,
        String parqueaderoNombre
) {}
