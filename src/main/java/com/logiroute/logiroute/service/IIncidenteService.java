package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.IncidenteDTO;
import com.logiroute.logiroute.model.Incidente;

import java.util.List;

public interface IIncidenteService {
    List<Incidente> listarTodos();
    List<Incidente> listarPorPedido(Long pedidoId);
    Incidente registrar(IncidenteDTO dto);
    long contar();
}
