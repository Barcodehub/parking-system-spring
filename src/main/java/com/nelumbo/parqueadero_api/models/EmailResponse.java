package com.nelumbo.parqueadero_api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {
    private String status;
    private String message;
    private LocalDateTime timestamp;
}