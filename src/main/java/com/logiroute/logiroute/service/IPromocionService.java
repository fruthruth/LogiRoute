package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PromocionDTO;
import com.logiroute.logiroute.model.Promocion;

import java.util.List;
import java.util.Optional;

public interface IPromocionService {

    List<Promocion> listarTodos();

    List<Promocion> listarActivasVigentes();

    Optional<Promocion> obtenerPorId(Long id);

    Promocion crear(PromocionDTO dto);

    Promocion actualizar(Long id, PromocionDTO dto);

    Promocion actualizarEstado(Long id, boolean activa);

    boolean eliminar(Long id);
}
