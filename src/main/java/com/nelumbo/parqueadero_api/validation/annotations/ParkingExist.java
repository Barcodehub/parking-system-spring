package com.nelumbo.parqueadero_api.validation.annotations;

import com.nelumbo.parqueadero_api.validation.validators.ParkingExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ParkingExistValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParkingExist {
    String message() default "El parqueadero no existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}