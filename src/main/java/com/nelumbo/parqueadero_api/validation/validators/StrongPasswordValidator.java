package com.nelumbo.parqueadero_api.validation.validators;

import com.nelumbo.parqueadero_api.validation.annotations.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return false;
        }

        // Expresión regular que verifica:
        // - Al menos 8 caracteres
        // - Al menos una mayúscula
        // - Al menos un número
        // - Al menos un carácter especial
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$";

        return password.matches(regex);
    }
}