package com.logiroute.logiroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidorAsignado;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EstadoVehiculo {
        DISPONIBLE, EN_USO, EN_MANTENIMIENTO
    }
}
