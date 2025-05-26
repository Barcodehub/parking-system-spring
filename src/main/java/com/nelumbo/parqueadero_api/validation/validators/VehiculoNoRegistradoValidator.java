package com.nelumbo.parqueadero_api.validation.validators;

import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.validation.annotations.VehiculoNoRegistradoActualmente;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehiculoNoRegistradoValidator implements
        ConstraintValidator<VehiculoNoRegistradoActualmente, String> {

    private final VehicleRepository vehicleRepository;


    @Override
    public boolean isValid(String placa, ConstraintValidatorContext context) {
        try {
            if (placa == null || placa.isBlank()) {
                return true;
            }

            String placaNormalizada = placa.toUpperCase().trim();
            return !vehicleRepository.existsByPlacaAndFechaSalidaIsNull(placaNormalizada);
        } catch (Exception e) {
            // Log the error
            return false;
        }
    }
}