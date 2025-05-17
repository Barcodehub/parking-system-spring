package com.nelumbo.parqueadero_api.validation.annotations;

import com.nelumbo.parqueadero_api.validation.validators.ValidPlacaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPlacaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlaca {
    String message() default "La placa debe tener 6 caracteres alfanuméricos sin ñ ni caracteres especiales Y EN MAYUSCULAS";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}