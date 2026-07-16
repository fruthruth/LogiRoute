package com.logiroute.logiroute.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    @NotBlank(message = "La dirección de origen es obligatoria")
    private String direccionOrigen;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
    private BigDecimal peso;

    @NotBlank(message = "El tipo de paquete es obligatorio")
    private String tipoPaquete;

    @DecimalMin(value = "-90.0", message = "La latitud mínima es -90")
    @DecimalMax(value = "90.0", message = "La latitud máxima es 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "La longitud mínima es -180")
    @DecimalMax(value = "180.0", message = "La longitud máxima es 180")
    private BigDecimal longitude;

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
}
