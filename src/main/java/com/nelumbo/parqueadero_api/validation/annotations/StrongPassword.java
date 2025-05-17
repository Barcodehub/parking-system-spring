package com.nelumbo.parqueadero_api.validation.annotations;

import com.nelumbo.parqueadero_api.validation.validators.StrongPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default """
        La contraseña debe tener:
        - Mínimo 8 caracteres
        - Al menos una mayúscula
        - Al menos un número
        - Al menos un carácter especial (!@#$%^&*)
        """;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}