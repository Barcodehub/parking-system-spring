package com.nelumbo.parqueadero_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulletinEmailDTO {
    private String email;
    private String name;
    private String message;
}

