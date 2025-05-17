package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.validation.annotations.ParkingExist;
import com.nelumbo.parqueadero_api.validation.annotations.ValidPlaca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

@Validated
public record VehicleExitRequestDTO(
        @NotBlank @ValidPlaca String placa,

        @ParkingExist
        Integer parqueaderoId
) {}