package com.nelumbo.parqueadero_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingResponseDTO(
        Integer id,
        String nombre,
        Integer capacidad,
        BigDecimal costoPorHora,
        Integer socioId,
        LocalDateTime createdAt
) {}