package com.nelumbo.parqueadero_api.controller;

import com.nelumbo.parqueadero_api.dto.AdminVehicleResponseDTO;
import com.nelumbo.parqueadero_api.dto.ParkingRequestDTO;
import com.nelumbo.parqueadero_api.dto.ParkingResponseDTO;
import com.nelumbo.parqueadero_api.repository.ParkingRepository;
import com.nelumbo.parqueadero_api.repository.VehicleRepository;
import com.nelumbo.parqueadero_api.services.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parkings")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;
    private final VehicleRepository vehicleRepository;
    private final ParkingRepository parkingRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "crear Parqueadero", description = "El Admin puede crear un parqueadero")
    public ParkingResponseDTO createParking(@RequestBody @Valid ParkingRequestDTO request) {
        return parkingService.createParking(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Parqueadero", description = "El Admin puede ver el detalle de un parqueadero especifico")
    public ParkingResponseDTO getParkingById(@PathVariable Integer id) {
        return parkingService.getParkingById(id);
    }

    @GetMapping
    @Operation(summary = "Listado - Parqueaderos", description = "El Admin puede ver la lista de parqueaderos")
    public List<ParkingResponseDTO> getAllParkings(
            @RequestParam(required = false) Integer socioId) {
        return parkingService.getAllParkings(Optional.ofNullable(socioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar Parqueaderos", description = "El Admin puede editar parqueaderos")
    public ParkingResponseDTO updateParking(
            @PathVariable Integer id,
            @RequestBody @Valid ParkingRequestDTO request) {
        return parkingService.updateParking(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar Parqueaderos", description = "El Admin puede eliminar parqueaderos")
    public void deleteParking(@PathVariable Integer id) {
        parkingService.deleteParking(Math.toIntExact(id));
    }




//que le pertenezca al socio autenticado

    @GetMapping("/socio/my-parkings/{parkingId}/vehicles")
    @Operation(summary = "Vehiculos de un Parqueadero de mi propiedad", description = "El Socio Puede ver listado/detalle de todos los vehículos en un parqueadero especifico que le pertenezca")
    public List<AdminVehicleResponseDTO> getVehiclesInMyParking(
            @PathVariable Integer parkingId,
            @RequestParam(required = false) Boolean activeOnly,
            @AuthenticationPrincipal UserDetails userDetails) {
        return parkingService.getVehiclesInMyParking(parkingId, activeOnly, userDetails);
    }



}