package com.nelumbo.parqueadero_api.dto;

import java.time.LocalDateTime;

public record VehicleDTO(
        Integer id,
        String placa,
        LocalDateTime fechaIngreso,
        String parqueaderoNombre
) {}