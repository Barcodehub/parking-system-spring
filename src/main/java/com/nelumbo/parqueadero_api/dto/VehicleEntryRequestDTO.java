package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.validation.annotations.ParqueaderoTieneEspacio;
import com.nelumbo.parqueadero_api.validation.annotations.ValidPlaca;
import com.nelumbo.parqueadero_api.validation.annotations.VehiculoNoRegistradoActualmente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ParqueaderoTieneEspacio
public record VehicleEntryRequestDTO(
        @NotBlank
        @ValidPlaca
        @VehiculoNoRegistradoActualmente
        String placa,

        @NotNull
        Long parqueaderoId
) {}