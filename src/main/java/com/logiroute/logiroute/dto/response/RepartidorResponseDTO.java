package com.logiroute.logiroute.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String licencia;
    private String estado;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
