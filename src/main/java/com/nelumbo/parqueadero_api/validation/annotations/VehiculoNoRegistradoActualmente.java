package com.nelumbo.parqueadero_api.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import com.nelumbo.parqueadero_api.validation.validators.VehiculoNoRegistradoValidator;

@Documented
@Constraint(validatedBy = VehiculoNoRegistradoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VehiculoNoRegistradoActualmente {
    String message() default "No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}