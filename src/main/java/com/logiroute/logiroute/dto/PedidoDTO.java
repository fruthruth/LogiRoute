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

    private Long clienteId;
}
