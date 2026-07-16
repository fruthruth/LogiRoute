package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.ClienteDTO;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Cliente;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.ClienteRepository;
import com.logiroute.logiroute.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

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

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorEmail(String email) {
        log.debug("Buscando cliente con email: {}", email);
        return clienteRepository.findByUsuarioEmailIgnoreCase(email.trim());
    }

    @Override
    @Transactional
    public Cliente crear(ClienteDTO dto) {
        log.info("Creando cliente para usuario id: {}", dto.getUsuarioId());
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", dto.getUsuarioId()));

        Cliente cliente = Cliente.builder()
                .usuario(usuario)
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, ClienteDTO dto) {
        log.info("Actualizando cliente id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));

        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        return clienteRepository.save(cliente);
    }
}
