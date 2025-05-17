package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.validation.annotations.ParkingExist;
import com.nelumbo.parqueadero_api.validation.annotations.ValidPlaca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleExitRequestDTO(
        @NotBlank @ValidPlaca String placa,

        @NotNull
        @ParkingExist
        Integer parqueaderoId
) {}