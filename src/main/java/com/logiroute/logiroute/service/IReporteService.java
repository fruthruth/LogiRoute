package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.model.Pedido;

import java.util.List;
import java.util.Optional;

public interface IReporteService {

    List<Pedido> filtrar(Integer dia, Integer mes, Integer anio,
                         String responsable, Double costoMin, Double costoMax);

    Optional<SeguimientoDTO> seguimiento(String codigo);
}
