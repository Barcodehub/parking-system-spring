package com.nelumbo.parqueadero_api.dto;

import java.math.BigDecimal;

public record ParkingTopEarningsDTO(
        String parkingName,
        BigDecimal totalEarnings
) {}