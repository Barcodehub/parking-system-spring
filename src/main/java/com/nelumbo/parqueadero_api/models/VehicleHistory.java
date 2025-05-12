package com.nelumbo.parqueadero_api.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_history")
@Data
public class VehicleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 6)
    private String placa;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    @ManyToOne
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parking parqueadero;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    private User socio;
}