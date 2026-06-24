package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.model.Pedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoService {

    List<Pedido> listarTodos();

    Optional<Pedido> obtenerPorId(Long id);

    Optional<Pedido> obtenerPorCodigo(String codigo);

    Pedido crear(PedidoDTO dto);

    Pedido actualizar(Long id, PedidoDTO dto);

    Optional<Pedido> actualizarEstado(Long id, String estado);

    boolean eliminar(Long id);

    long contar();
}
