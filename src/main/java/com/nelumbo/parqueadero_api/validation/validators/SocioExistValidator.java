package com.nelumbo.parqueadero_api.validation.validators;

import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.validation.annotations.SocioExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocioExistValidator implements ConstraintValidator<SocioExist, Integer> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(Integer socioId, ConstraintValidatorContext context) {
        if (socioId == null) {
            return true; // Permitir null, usar @NotNull para hacerlo obligatorio
        }
        return userRepository.existsById(socioId);
    }
}