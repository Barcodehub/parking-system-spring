package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.BusinessException;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.dto.errors.ResponseMessages;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    // Crear
    public SuccessResponseDTO<ParkingResponseDTO> createParking(ParkingRequestDTO request){
        User socio = validateAndGetSocio(Long.valueOf(request.socioId()));

        validateSocioRole(socio);

        Parking parking = mapToEntity(request, socio);
        Parking savedParking = parkingRepository.save(parking);
        return new SuccessResponseDTO<>(mapToDTO(savedParking));
    }



    // Obtener por ID
    public SuccessResponseDTO<ParkingResponseDTO> getParkingById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de parqueadero inválido");
        }
        Parking parking = parkingExist(id);
        return new SuccessResponseDTO<>(mapToDTO(parking));
    }


    // Listar todos (con filtro opcional por socio)
    public SuccessResponseDTO<List<ParkingResponseDTO>> getAllParkingsFiltered(UserDetails userDetails) {
        boolean isSocio = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SOCIO"));

        List<Parking> parkings;

        if (isSocio) {
            parkings = parkingRepository.findBySocioEmail(userDetails.getUsername());
        } else {
            parkings = parkingRepository.findAll();
        }

        List<ParkingResponseDTO> responseList = parkings.stream()
                .map(this::mapToDTO)
                .toList();
        return responseList.isEmpty()
                ? new SuccessResponseDTO<>(null, ResponseMessages.NO_PARKING)
                : new SuccessResponseDTO<>(responseList);
    }


    // Actualizar
    public SuccessResponseDTO<ParkingResponseDTO> updateParking(Integer id, @Valid ParkingRequestDTO request) {

        User socio = validateAndGetSocio(Long.valueOf(request.socioId()));
        Parking parking = parkingExist(id);

        // Actualización de campos
        parking.setNombre(request.nombre());
        parking.setCapacidad(request.capacidad());
        parking.setCostoPorHora(request.costoPorHora());
        parking.setSocio(socio);

        Parking updatedParking = parkingRepository.save(parking);
        return new SuccessResponseDTO<>(mapToDTO(updatedParking));
    }

    // Eliminar
    public SuccessResponseDTO<Void> deleteParking(Integer id) {
        Parking parking = parkingExist(id);
        parkingRepository.delete(parking);
        return new SuccessResponseDTO<>(null);
    }



    public SuccessResponseDTO<List<AdminVehicleResponseDTO>> getVehiclesInParking(
            Integer parkingId,
            @AuthenticationPrincipal UserDetails userDetails) {  // userDetails puede ser null (para admin)

        // 1. Obtener parqueadero
        Parking parking = parkingExist(parkingId);

        // 2. Validar permisos
        validateParkingAccess(parking, userDetails);

        // 3. Obtener vehículos
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId);

        if (vehicles.isEmpty()) {
            return new SuccessResponseDTO<>(null, ResponseMessages.No_VEH_IN_PARKING);
        }

        // 4. Convertir a DTOs
        List<AdminVehicleResponseDTO> vehicleDTOs = vehicles.stream()
                .map(this::convertToAdminVehicleDTO)
                .toList();

        // 5. Retornar respuesta
        return new SuccessResponseDTO<>(vehicleDTOs);
    }


    // -- Métodos auxiliares --
    @Generated
    private void validateSocioRole(User user) {
        if (user.getRole() != Role.SOCIO) {
            throw new BusinessException("El usuario debe tener rol SOCIO", "socioId");
        }
    }

    @Generated
    private Parking mapToEntity(ParkingRequestDTO dto, User socio) {
        return Parking.builder()
                .nombre(dto.nombre())
                .capacidad(dto.capacidad())
                .costoPorHora(dto.costoPorHora())
                .socio(socio)
                .build();
    }
    @Generated
    private ParkingResponseDTO mapToDTO(Parking parking) {
        return new ParkingResponseDTO(
                parking.getId(),
                parking.getNombre(),
                parking.getCapacidad(),
                parking.getCostoPorHora(),
                parking.getSocio().getId(),
                parking.getCreatedAt()
        );
    }

    @Generated
    private void validateParkingAccess(Parking parking, UserDetails userDetails) {
        if (userDetails == null) {
            return; // Acceso admin sin restricciones
        }

        if (isSocio(userDetails)){
            User currentUser = (User) userDetails;
            if (!parking.getSocio().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("No tienes acceso a este parqueadero");
            }
        }
    }
    @Generated
    private boolean isSocio(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SOCIO"));
    }
    @Generated
    private AdminVehicleResponseDTO convertToAdminVehicleDTO(Vehicle vehicle) {
        return new AdminVehicleResponseDTO(
                vehicle.getId(),
                vehicle.getPlaca(),
                vehicle.getFechaIngreso(),
                vehicle.getFechaSalida(),
                vehicle.getParqueadero().getNombre(),
                vehicle.getSocio().getName()
        );
    }

    @Generated
    private Parking parkingExist(Integer id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));
    }
    @Generated
    private User validateAndGetSocio(Long socioId) {
        if (socioId == null) {
            throw new IllegalArgumentException("El ID del socio no puede ser nulo");
        }

        return userRepository.findById(Math.toIntExact(socioId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No se encontró un socio con ID %d", socioId)
                ));
    }
}