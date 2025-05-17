package com.nelumbo.parqueadero_api.validation.validators;


import com.nelumbo.parqueadero_api.validation.annotations.ValidPlaca;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPlacaValidator implements ConstraintValidator<ValidPlaca, String> {

    private static final String PLACA_REGEX = "^[A-HJ-NP-TV-Z0-9]{6}$";

    @Override
    public boolean isValid(String placa, ConstraintValidatorContext context) {
        if (placa == null || placa.isBlank()) {
            return false;
        }

        // Validación de longitud exacta de 6 caracteres
        if (placa.length() != 6) {
            return false;
        }

        // Validación de caracteres alfanuméricos sin ñ ni especiales
        return placa.matches(PLACA_REGEX);
    }
}