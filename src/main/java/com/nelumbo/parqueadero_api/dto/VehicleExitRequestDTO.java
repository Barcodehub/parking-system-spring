package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VehicleExitRequestDTO(
        @NotBlank
        @Size(min = 6, max = 6, message = "La placa debe tener 6 caracteres")
        String placa
) {}