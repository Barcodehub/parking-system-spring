package com.nelumbo.parqueadero_api.controller;


import com.nelumbo.parqueadero_api.dto.UserRequestDTO;
import com.nelumbo.parqueadero_api.dto.UserResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.SuccessResponseDTO;
import com.nelumbo.parqueadero_api.services.ParkingService;
import com.nelumbo.parqueadero_api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    




    }








