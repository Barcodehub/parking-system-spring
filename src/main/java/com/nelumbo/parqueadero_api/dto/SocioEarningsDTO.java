package com.nelumbo.parqueadero_api.dto;

import java.math.BigDecimal;

public record SocioEarningsDTO(
        String socioName,
        Long vehicleCount,
        BigDecimal totalEarnings
) {}


