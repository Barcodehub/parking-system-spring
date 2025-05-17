package com.nelumbo.parqueadero_api.dto.errors;

import java.util.List;

public record ValidationDataDTO(
        String flag,
        List<WarningDTO> warnings,
        List<RejectionDTO> rejections
) {}