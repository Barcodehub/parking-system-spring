package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.validation.annotations.ParqueaderoTieneEspacio;
import com.nelumbo.parqueadero_api.validation.annotations.ValidPlaca;
import com.nelumbo.parqueadero_api.validation.annotations.VehiculoNoRegistradoActualmente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@ParqueaderoTieneEspacio
public record VehicleEntryRequestDTO(
        @NotBlank
        @ValidPlaca
        @VehiculoNoRegistradoActualmente
        String placa,
        Integer parqueaderoId
) {}