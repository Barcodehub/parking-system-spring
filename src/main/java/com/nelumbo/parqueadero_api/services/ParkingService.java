package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.exception.BusinessException;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.validation.annotations.ParkingExist;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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
        User socio = userRepository.findById(Math.toIntExact(request.socioId()))
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        validateSocioRole(socio);
        validateUniqueParkingName(request.nombre(), request.socioId());

        Parking parking = mapToEntity(request, socio);
        Parking savedParking = parkingRepository.save(parking);
        return new SuccessResponseDTO<>(mapToDTO(savedParking));
    }

    // Obtener por ID
    public SuccessResponseDTO<ParkingResponseDTO> getParkingById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de parqueadero inválido");
        }
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));
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
            parkings = parkingRepository.findAll(); // ADMIN o otros roles ven todo
        }

        List<ParkingResponseDTO> responseList = parkings.stream()
                .map(this::mapToDTO)
                .toList();

        return new SuccessResponseDTO<>(responseList);
    }

    public SuccessResponseDTO<List<ParkingResponseDTO>> getParkingsBySocioEmail(String email) {
        List<Parking> parkings = parkingRepository.findBySocioEmail(email);

        if (parkings.isEmpty()) {
            throw new ResourceNotFoundException("No tienes parqueaderos asociados");
        }

        List<ParkingResponseDTO> responseList = parkings.stream()
                .map(this::mapToDTO)
                .toList();

        return new SuccessResponseDTO<>(responseList);
    }


    // Actualizar
    public SuccessResponseDTO<ParkingResponseDTO> updateParking(@ParkingExist @PathVariable Integer id, @Valid ParkingRequestDTO request) {

        Parking parking = parkingRepository.getReferenceById(id);

        // Validación de nombre único
        validateUniqueParkingName(
                request.nombre(),
                request.socioId()
        );

        // Actualización de campos
        parking.setNombre(request.nombre());
        parking.setCapacidad(request.capacidad());
        parking.setCostoPorHora(request.costoPorHora());
        User socio = userRepository.getReferenceById(request.socioId());
        parking.setSocio(socio);

        Parking updatedParking = parkingRepository.save(parking);
        return new SuccessResponseDTO<>(mapToDTO(updatedParking));
    }

    // Eliminar
    public SuccessResponseDTO<Void> deleteParking(Integer id) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        parkingRepository.delete(parking);

        return new SuccessResponseDTO<>(null); // Data null para operaciones de eliminación exitosas
    }

    // -- Métodos auxiliares --
    private void validateSocioRole(User user) {
        if (user.getRole() != Role.SOCIO) {
            throw new BusinessException("El usuario debe tener rol SOCIO", "socioId");
        }
    }

    private void validateUniqueParkingName(String nombre, Integer socioId) {
        if (parkingRepository.existsByNombreAndSocioId(nombre, socioId)) {
            throw new BusinessException("Ya tienes un parqueadero con ese nombre", "nombre");
        }
    }


    private Parking mapToEntity(ParkingRequestDTO dto, User socio) {
        return Parking.builder()
                .nombre(dto.nombre())
                .capacidad(dto.capacidad())
                .costoPorHora(dto.costoPorHora())
                .socio(socio)
                .build();
    }

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





    public SuccessResponseDTO<List<AdminVehicleResponseDTO>> getVehiclesInParking(
            @ParkingExist @PathVariable Integer parkingId,
            @AuthenticationPrincipal UserDetails userDetails) {  // userDetails puede ser null (para admin)

        // 1. Obtener parqueadero (la existencia ya está validada por @ParkingExist)
        Parking parking = parkingRepository.getReferenceById(parkingId);

        // 2. Validar permisos
        validateParkingAccess(parking, userDetails);

        // 3. Obtener vehículos
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId);

        if (vehicles.isEmpty()) {
            throw new ResourceNotFoundException("No hay vehículos estacionados");
        }

        // 4. Convertir a DTOs
        List<AdminVehicleResponseDTO> vehicleDTOs = vehicles.stream()
                .map(this::convertToAdminVehicleDTO)
                .toList();

        // 5. Retornar respuesta
        return new SuccessResponseDTO<>(vehicleDTOs);
    }




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

    private boolean isSocio(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SOCIO"));
    }

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




}