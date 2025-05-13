package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ParkingRequestDTO(
        String nombre,
        @Positive Integer capacidad,
        @Positive BigDecimal costoPorHora,
        @NotNull Integer socioId  // ID del usuario socio
) {}