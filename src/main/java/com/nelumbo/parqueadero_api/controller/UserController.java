package com.nelumbo.parqueadero_api.controller;


import com.nelumbo.parqueadero_api.dto.BulletinEmailDTO;
import com.nelumbo.parqueadero_api.dto.UserRequestDTO;
import com.nelumbo.parqueadero_api.dto.UserResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.models.Role;
import com.nelumbo.parqueadero_api.repository.UserRepository;
import com.nelumbo.parqueadero_api.services.ParkingService;
import com.nelumbo.parqueadero_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ParkingService parkingService;

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequest) {
        SuccessResponseDTO<UserResponseDTO> response = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private final UserRepository userRepository;

    @GetMapping("/socios")
    public List<BulletinEmailDTO> getSocios() {
        return userRepository.findByRole(Role.SOCIO)
                .stream()
                .map(user -> new BulletinEmailDTO(user.getEmail(), user.getName(), ""))
                .collect(Collectors.toList());
    }



    }








