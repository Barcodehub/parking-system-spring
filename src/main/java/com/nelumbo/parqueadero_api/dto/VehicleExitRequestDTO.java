package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleExitRequestDTO(
        @NotBlank String placa,
        @NotNull Integer parqueaderoId
) {}