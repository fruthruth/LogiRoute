package com.logiroute.logiroute.dto;

import jakarta.validation.constraints.*;
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
public class PromocionDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.1", message = "El descuento debe ser mayor a 0")
    @DecimalMax(value = "100", message = "El descuento no puede superar el 100%")
    private BigDecimal descuentoPorcentaje;

    @DecimalMin(value = "0", message = "El monto mínimo no puede ser negativo")
    private BigDecimal montoMinimo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;
}
