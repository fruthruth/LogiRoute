package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;

public interface IAsignacionService {

    void asignar(AsignacionDTO dto);

    void completarEntrega(Long pedidoId);
}
