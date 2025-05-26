package com.nelumbo.parqueadero_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


    // Método para asegurar mayúsculas antes de persistir
    @PrePersist
    @PreUpdate
    private void normalizePlaca() {
        if (this.placa != null) {
            this.placa = this.placa.toUpperCase().trim();
        }
    }


    @ManyToOne
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parking parqueadero;

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    private User socio;
}