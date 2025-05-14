package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleEntryRequestDTO(
        @NotBlank
        @Size(min = 6, max = 6, message = "La placa debe tener 6 caracteres")
        @Pattern(regexp = "^[A-Z0-9]*$", message = "La placa solo puede contener letras mayúsculas (A-Z) y números (0-9), sin Ñ ni caracteres especiales")
        String placa,

        @NotNull
        Long parqueaderoId
) {}