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
public class PedidoResponseDTO {

    private Long id;
    private String codigo;
    private String clienteNombre;
    private String repartidorNombre;
    private String direccionOrigen;
    private String direccionDestino;
    private BigDecimal peso;
    private String tipoPaquete;
    private String estado;
    private BigDecimal costo;
    private LocalDateTime fechaEstimada;
    private LocalDateTime fechaEntrega;
    private LocalDateTime createdAt;
}
