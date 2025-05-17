package com.nelumbo.parqueadero_api.validation.annotations;

import com.nelumbo.parqueadero_api.validation.validators.SocioExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SocioExistValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SocioExist {
    String message() default "El socio no existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}