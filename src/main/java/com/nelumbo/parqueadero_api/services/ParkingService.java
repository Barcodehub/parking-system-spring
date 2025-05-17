package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.ValidationDataDTO;
import com.nelumbo.parqueadero_api.dto.errors.VehicleValidationResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.WarningDTO;
import com.nelumbo.parqueadero_api.exception.BusinessException;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.models.User;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SOCIO")); // Asegúrate de que el rol tenga el prefijo correcto (ej. "ROLE_")

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
    public SuccessResponseDTO<ParkingResponseDTO> updateParking(Integer id, ParkingRequestDTO request) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

//        if (!parking.getSocio().getId().equals(request.socioId())) {
//            throw new BusinessException("No puedes modificar un parqueadero de otro socio");
//        }


        if (request.socioId() != null) {
            User socio = userRepository.findById(request.socioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Socio con ID %d no encontrado", request.socioId()))
                    );

            // 3. Validar nombre único (excepto para el registro actual)
            validateUniqueParkingName(
                    request.nombre(),
                    request.socioId(),
                    id
            );

            parking.setSocio(socio);
        } else {
            // Si no viene socioId, mantener el socio actual
            validateUniqueParkingName(
                    request.nombre(),
                    parking.getSocio() != null ? parking.getSocio().getId() : null,
                    id
            );
        }

        parking.setNombre(request.nombre());
        parking.setCapacidad(request.capacidad());
        parking.setCostoPorHora(request.costoPorHora());
        if (request.socioId() != null) {
            User socio = userRepository.findById(request.socioId())
                    .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado"));
            parking.setSocio(socio);
        }
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
            throw new BusinessException("El usuario debe tener rol SOCIO");
        }
    }

    private void validateUniqueParkingName(String nombre, Integer socioId) {
        if (parkingRepository.existsByNombreAndSocioId(nombre, socioId)) {
            throw new BusinessException("Ya tienes un parqueadero con ese nombre");
        }
    }

    private void validateUniqueParkingName(String nombre, Integer socioId, Integer parkingId) {
        if (parkingRepository.existsByNombreAndSocioIdAndIdNot(nombre, socioId, parkingId)) {
            throw new BusinessException("Ya tienes un parqueadero con ese nombre");
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





    public SuccessResponseDTO<VehicleValidationResponseDTO> getVehiclesInParking(
            Integer parkingId,
            UserDetails userDetails) {  // userDetails puede ser null (para admin)

        // 1. Verificar si el parqueadero existe
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        // 2. Si el usuario es SOCIO, verificar que el parqueadero le pertenezca
        if (userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SOCIO"))) {

            User currentUser = (User) userDetails;
            if (!parking.getSocio().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("No tienes acceso a este parqueadero");
            }
        }

        // 3. Obtener vehículos (lógica común)
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId);
        List<AdminVehicleResponseDTO> vehicleDTOs = vehicles.stream()
                .map(this::convertToAdminVehicleDTO)
                .toList();

        // 4. Construir respuesta (adaptable para ambos casos)
        List<WarningDTO> warnings = new ArrayList<>();
        if (vehicles.isEmpty()) {
            warnings.add(new WarningDTO("VEH_001", "No hay vehículos estacionados"));
        }

        VehicleValidationResponseDTO responseData = new VehicleValidationResponseDTO(
                vehicleDTOs,
                vehicles.isEmpty() ? "AMARILLA" : "VERDE",
                warnings,
                Collections.emptyList()
        );

        return new SuccessResponseDTO<>(responseData);
    }









    //vehiculos de un parking
    public List<AdminVehicleResponseDTO> getVehiclesInParking(Integer parkingId) {
        // Verificar existencia del parqueadero
        parkingRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        // Obtener solo vehículos actualmente parqueados
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId);

        if (vehicles.isEmpty()) {
            throw new ResourceNotFoundException(
                    "El parqueadero no tiene vehículos estacionados actualmente"
            );
        }

        return vehicles.stream()
                .map(this::convertToAdminVehicleDTO)
                .toList();
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




    public SuccessResponseDTO<VehicleValidationResponseDTO> getVehiclesInMyParking(
            Integer parkingId,
            UserDetails userDetails) {

        // Obtener el usuario autenticado
        User currentUser = (User) userDetails;

        // Buscar el parqueadero y verificar existencia
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Parqueadero con ID %d no encontrado", parkingId)));

        // Verificar propiedad del parqueadero
        if (!parking.getSocio().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "El parqueadero no pertenece a tu cuenta");
        }

        // Obtener vehículos en el parqueadero
        List<Vehicle> vehicles = vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId);

        List<AdminVehicleResponseDTO> responseList = vehicles.stream()
                .map(this::convertToAdminVehicleDTO)
                .toList();

        // Opcional: agregar validaciones/warnings si es necesario
        List<WarningDTO> warnings = new ArrayList<>();
        if (responseList.isEmpty()) {
            warnings.add(new WarningDTO("VEH_001", "No hay vehículos en este parqueadero"));
        }

        // Opción 1: Retorno simple con lista de vehículos
       // return new SuccessResponseDTO<>(responseList);

        // Opción 2: Si necesitas incluir validaciones

        VehicleValidationResponseDTO responseData = new VehicleValidationResponseDTO(
                responseList,
                vehicles.isEmpty() ? "AMARILLA" : "VERDE",
                warnings,
                Collections.emptyList()
        );

        return new SuccessResponseDTO<>(responseData);

    }
}