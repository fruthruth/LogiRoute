package com.logiroute.logiroute.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoDTO {

    private Long pedidoId;
    private String codigo;
    private String estado;
    private String direccionOrigen;
    private String direccionDestino;
    private String repartidorNombre;
    private LocalDateTime fechaEstimada;
    private LocalDateTime fechaEntrega;
}
