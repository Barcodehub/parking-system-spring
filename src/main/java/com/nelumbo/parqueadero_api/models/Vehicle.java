package com.nelumbo.parqueadero_api.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 6)
    private String placa;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso = LocalDateTime.now();

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @ManyToOne
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parking parqueadero;

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    private User socio;
}