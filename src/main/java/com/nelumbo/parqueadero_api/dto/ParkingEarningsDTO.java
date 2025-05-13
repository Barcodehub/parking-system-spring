package com.nelumbo.parqueadero_api.dto;

import java.math.BigDecimal;

public record ParkingEarningsDTO(
        BigDecimal today,
        BigDecimal week,
        BigDecimal month,
        BigDecimal year
) {}

