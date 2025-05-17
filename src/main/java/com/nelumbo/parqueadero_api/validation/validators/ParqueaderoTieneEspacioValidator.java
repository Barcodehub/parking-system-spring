package com.nelumbo.parqueadero_api.validation.validators;

import com.nelumbo.parqueadero_api.dto.VehicleEntryRequestDTO;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.validation.annotations.ParqueaderoTieneEspacio;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParqueaderoTieneEspacioValidator implements
        ConstraintValidator<ParqueaderoTieneEspacio, VehicleEntryRequestDTO> {

    private final ParkingRepository parkingRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public boolean isValid(VehicleEntryRequestDTO request, ConstraintValidatorContext context) {
        if (request == null || request.parqueaderoId() == null) {
            return true;
        }

        return parkingRepository.findById(request.parqueaderoId().intValue())
                .map(parqueadero -> {
                    long vehiculosActivos = vehicleRepository.countActiveVehiclesInParking(Math.toIntExact(request.parqueaderoId()));
                    boolean tieneEspacio = vehiculosActivos < parqueadero.getCapacidad();

                    if (!tieneEspacio) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                                .addPropertyNode("parqueaderoId")
                                .addConstraintViolation();
                    }
                    return tieneEspacio;
                })
                .orElse(true);
    }
}