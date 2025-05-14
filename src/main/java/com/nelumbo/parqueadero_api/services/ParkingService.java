package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.VehicleResponseDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public ParkingResponseDTO createParking(ParkingRequestDTO request) {
        User socio = userRepository.findById(Math.toIntExact(request.socioId()))
                .orElseThrow(() -> new ResourceNotFoundException("Socio no encontrado"));

        validateSocioRole(socio);
        validateUniqueParkingName(request.nombre(), request.socioId());

        Parking parking = mapToEntity(request, socio);
        Parking savedParking = parkingRepository.save(parking);
        return mapToDTO(savedParking);
    }

    // Obtener por ID
    public ParkingResponseDTO getParkingById(Integer id) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));
        return mapToDTO(parking);
    }

    // Listar todos (con filtro opcional por socio)
    public List<ParkingResponseDTO> getAllParkings(Optional<Integer> socioId) {
        List<Parking> parkings = socioId
                .map(parkingRepository::findBySocioId)
                .orElseGet(parkingRepository::findAll);
        return parkings.stream().map(this::mapToDTO).toList();
    }

    // Actualizar
    public ParkingResponseDTO updateParking(Integer id, ParkingRequestDTO request) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        if (!parking.getSocio().getId().equals(request.socioId())) {
            throw new BusinessException("No puedes modificar un parqueadero de otro socio");
        }

        validateUniqueParkingName(request.nombre(), Math.toIntExact(request.socioId()), id);

        parking.setNombre(request.nombre());
        parking.setCapacidad(request.capacidad());
        parking.setCostoPorHora(request.costoPorHora());

        return mapToDTO(parkingRepository.save(parking));
    }

    // Eliminar
    public void deleteParking(Integer id) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));
        parkingRepository.delete(parking);
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


    //vehiculos de un parking
    public List<AdminVehicleResponseDTO> getVehiclesInParking(Integer parkingId, Boolean activeOnly) {
        // Verificación de autorización
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Requiere rol ADMIN");
        }

        // Verificar existencia del parqueadero
        parkingRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado"));

        // Consulta flexible (activos o todos)
        List<Vehicle> vehicles = (activeOnly != null && activeOnly) ?
                vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId) :
                vehicleRepository.findByParqueaderoId(parkingId);

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

    public List<ParkingResponseDTO> getParkingsBySocio(String email) {
        // Verificación de autorización
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SOCIO"))) {
            throw new AccessDeniedException("Requiere rol SOCIO");
        }

        return parkingRepository.findBySocioEmail(email).stream()
                .map(this::convertToParkingDTO)
                .toList();
    }

    private ParkingResponseDTO convertToParkingDTO(Parking parking) {
        return new ParkingResponseDTO(
                parking.getId(),
                parking.getNombre(),
                parking.getCapacidad(),
                parking.getCostoPorHora(),
                parking.getSocio().getId(),
                parking.getCreatedAt()
        );
    }





    public List<AdminVehicleResponseDTO> getVehiclesInMyParking(Integer parkingId, Boolean activeOnly, UserDetails userDetails) {
        // Obtener el usuario autenticado
        User currentUser = (User) userDetails;

        // Buscar el parqueadero y verificar que pertenece al socio
        Parking parking = parkingRepository.findByIdAndSocioId(parkingId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Parqueadero no encontrado o no tienes permisos"));

        // Consulta de vehículos (solo para este parqueadero del socio)
        List<Vehicle> vehicles = (activeOnly != null && activeOnly) ?
                vehicleRepository.findByParqueaderoIdAndFechaSalidaIsNull(parkingId) :
                vehicleRepository.findByParqueaderoId(parkingId);

        return vehicles.stream()
                .map(this::convertToAdminVehicleDTO)
                .toList();
    }
}