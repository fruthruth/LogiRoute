package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.model.Repartidor;

public interface IAsignacionService {

    void asignar(AsignacionDTO dto);

    /**
     * Selecciona automáticamente al repartidor más conveniente considerando
     * disponibilidad, capacidad del vehículo, carga activa y cercanía.
     *
     * @return repartidor finalmente asignado
     */
    Repartidor autoAsignar(Long pedidoId);

    void completarEntrega(Long pedidoId);
}
