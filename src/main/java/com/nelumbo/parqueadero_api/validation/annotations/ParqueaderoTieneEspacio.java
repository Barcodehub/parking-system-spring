package com.nelumbo.parqueadero_api.validation.annotations;

import com.nelumbo.parqueadero_api.validation.validators.ParqueaderoTieneEspacioValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ParqueaderoTieneEspacioValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParqueaderoTieneEspacio {
    String message() default "El parqueadero ha alcanzado su capacidad máxima";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}