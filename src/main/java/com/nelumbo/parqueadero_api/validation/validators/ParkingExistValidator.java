package com.nelumbo.parqueadero_api.validation.validators;

import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.validation.annotations.ParkingExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkingExistValidator implements ConstraintValidator<ParkingExist, Integer> { // Cambiado a Integer

    private final ParkingRepository parkingRepository;

    @Override
    public boolean isValid(Integer parkingId, ConstraintValidatorContext context) {
        if (parkingId == null) {
            return false;
        }
        try {
            return parkingRepository.existsById(parkingId);
        } catch (Exception e) {
            // Log del error
            return false;
        }
    }
}