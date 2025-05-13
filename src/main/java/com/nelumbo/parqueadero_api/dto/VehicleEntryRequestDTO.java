package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleEntryRequestDTO(
        @NotBlank
        @Size(min = 6, max = 6, message = "La placa debe tener 6 caracteres")
        String placa,

        @NotNull
        Long parqueaderoId
) {}