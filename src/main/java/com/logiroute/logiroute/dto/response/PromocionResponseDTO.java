package com.logiroute.logiroute.dto.response;

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
public class PromocionResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal montoMinimo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activa;
    private LocalDateTime createdAt;
}
