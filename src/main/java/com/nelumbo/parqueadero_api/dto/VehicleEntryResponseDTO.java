package com.nelumbo.parqueadero_api.dto;

import com.nelumbo.parqueadero_api.dto.errors.RejectionDTO;
import com.nelumbo.parqueadero_api.dto.errors.WarningDTO;

import java.util.List;

public record VehicleEntryResponseDTO(
        String flag,
        List<WarningDTO> warnings,
        List<RejectionDTO> rejections,
        Integer vehicleId
) {}