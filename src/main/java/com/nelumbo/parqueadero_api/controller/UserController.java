package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.dto.UserRequestDTO;
import com.nelumbo.parqueadero_api.dto.UserResponseDTO;
import com.nelumbo.parqueadero_api.exception.ResourceNotFoundException;
import com.nelumbo.parqueadero_api.models.Parking;
import com.nelumbo.parqueadero_api.models.Vehicle;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ParkingRepository parkingRepository;
    private final VehicleRepository vehicleRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        UserResponseDTO createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    @GetMapping("/socio/parkings")
    public List<ParkingResponseDTO> getParkingsBySocio(
            @AuthenticationPrincipal UserDetails userDetails) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SOCIO"))) {
            throw new AccessDeniedException("Requiere rol USER");
        }

        return parkingRepository.findBySocioEmail(userDetails.getUsername()).stream()
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





     @GetMapping("/parkings/{parkingId}/vehicles")
     public List<AdminVehicleResponseDTO> getVehiclesInParking(
             @PathVariable Integer parkingId,
             @RequestParam(required = false) Boolean activeOnly) {

         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
             throw new AccessDeniedException("Requiere rol SOCIO");
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
    }








