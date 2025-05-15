package com.nelumbo.parqueadero_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @Email(message = "Formato de correo inválido")
    private String email;
    @Size(min = 6, max = 6, message = "La placa debe tener 6 caracteres")
    private String placa;
    @NotBlank(message = "El campo mensaje no puede estar vacío")
    private String message;
    private Integer parqueaderoId;
}