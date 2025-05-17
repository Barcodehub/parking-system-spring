package com.nelumbo.parqueadero_api.validation.validators;

import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.validation.annotations.ParkingExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkingExistValidator implements ConstraintValidator<ParkingExist, Integer> { // Cambiado a Integer

    private final ParkingRepository parkingRepository;

    @Override
    public boolean isValid(Integer parkingId, ConstraintValidatorContext context) {
        if (parkingId == null) {
            buildValidationError(context, "El ID de parqueadero no puede ser nulo");
            return false;
        }

        try {
            if (!parkingRepository.existsById(parkingId)) {
                buildValidationError(context, "No existe un parqueadero con ID " + parkingId);
                return false;
            }
            return true;
        } catch (Exception e) {
            buildValidationError(context, "Error al validar el parqueadero: " + e.getMessage());
            return false;
        }
    }

    private void buildValidationError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
