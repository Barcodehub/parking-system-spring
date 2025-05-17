package com.nelumbo.parqueadero_api.dto.errors;

public record ErrorDetailDTO(
        String code,
        String description,
        String field,
        String traceId
) {}