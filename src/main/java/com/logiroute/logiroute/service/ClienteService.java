package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Cliente;
import com.logiroute.logiroute.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerPorUsuarioId(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId);
    }
}
