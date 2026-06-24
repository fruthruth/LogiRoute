package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Cliente;
import com.logiroute.logiroute.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.debug("Listando todos los clientes");
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long id) {
        log.debug("Buscando cliente con id: {}", id);
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorUsuarioId(Long usuarioId) {
        log.debug("Buscando cliente con usuarioId: {}", usuarioId);
        return clienteRepository.findByUsuarioId(usuarioId);
    }
}
