package com.nelumbo.parqueadero_api.dto.errors;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;

import java.util.List;

public record VehicleValidationResponseDTO(
        List<AdminVehicleResponseDTO> vehicles,
        String flag,
        List<WarningDTO> warnings,
        List<RejectionDTO> rejections
) {}