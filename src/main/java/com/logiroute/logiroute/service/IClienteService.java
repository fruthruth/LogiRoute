package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.ClienteDTO;
import com.logiroute.logiroute.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface IClienteService {

    List<Cliente> listarTodos();

    Optional<Cliente> obtenerPorId(Long id);

    Optional<Cliente> obtenerPorUsuarioId(Long usuarioId);

    Optional<Cliente> obtenerPorEmail(String email);

    Cliente crear(ClienteDTO dto);

    Cliente actualizar(Long id, ClienteDTO dto);
}
