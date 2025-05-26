package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.validation.annotations.ValidPlaca;
import jakarta.validation.constraints.NotBlank;


public record VehicleExitRequestDTO(
        @NotBlank @ValidPlaca String placa,
        Integer parqueaderoId
) {}