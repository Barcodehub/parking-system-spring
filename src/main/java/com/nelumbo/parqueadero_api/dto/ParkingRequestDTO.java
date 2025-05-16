package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ParkingRequestDTO(
        @NotBlank(message = "Name is required")
        String nombre,
        @Positive Integer capacidad,
        @Positive BigDecimal costoPorHora,
        @NotNull(message = "Socio is required")
        Integer socioId  // ID del usuario socio
) {}