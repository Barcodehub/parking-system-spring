package com.nelumbo.parqueadero_api.dto;

import java.time.LocalDateTime;

public record AdminVehicleResponseDTO(
        Integer id,
        String placa,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaSalida,
        String parqueaderoNombre,
        String socioNombre
) {}