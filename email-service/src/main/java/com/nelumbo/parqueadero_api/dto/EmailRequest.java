package com.nelumbo.parqueadero_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String email;
    private String placa;
    private String message;
    private String parqueaderoId;
}