package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.*;
import com.logiroute.logiroute.utils.CodigoGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService implements IPedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RepartidorRepository repartidorRepository;
    private final CodigoGenerator codigoGenerator;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        log.debug("Listando todos los pedidos");
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorClienteId(Long clienteId) {
        log.debug("Listando pedidos del cliente id: {}", clienteId);
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorRepartidorId(Long repartidorId) {
        log.debug("Listando pedidos del repartidor id: {}", repartidorId);
        return pedidoRepository.findByRepartidorId(repartidorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorId(Long id) {
        log.debug("Buscando pedido con id: {}", id);
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorCodigo(String codigo) {
        log.debug("Buscando pedido con código: {}", codigo);
        return pedidoRepository.findByCodigo(codigo);
    }

    @Override
    @Transactional
    public Pedido crear(PedidoDTO dto) {
        log.info("Creando nuevo pedido para cliente id: {}", dto.getClienteId());
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", dto.getClienteId()));

        Pedido pedido = Pedido.builder()
                .codigo(codigoGenerator.generarCodigoPedido())
                .cliente(cliente)
                .direccionOrigen(dto.getDireccionOrigen())
                .direccionDestino(dto.getDireccionDestino())
                .peso(dto.getPeso())
                .tipoPaquete(dto.getTipoPaquete())
                .estado(EstadoPedido.PENDIENTE)
                .build();

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido creado con código: {}", guardado.getCodigo());
        return guardado;
    }

    @Override
    @Transactional
    public Pedido actualizar(Long id, PedidoDTO dto) {
        log.info("Actualizando pedido id: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", dto.getClienteId()));

        pedido.setDireccionOrigen(dto.getDireccionOrigen());
        pedido.setDireccionDestino(dto.getDireccionDestino());
        pedido.setPeso(dto.getPeso());
        pedido.setTipoPaquete(dto.getTipoPaquete());
        pedido.setCliente(cliente);

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Optional<Pedido> actualizarEstado(Long id, String estado) {
        log.info("Actualizando estado del pedido id: {} a {}", id, estado);
        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("Estado inválido: " + estado);
        }

        return pedidoRepository.findById(id).map(pedido -> {
            if (!pedido.getEstado().esTransicionValida(nuevoEstado)) {
                throw new EstadoInvalidoException(
                        "Transición inválida: " + pedido.getEstado() + " → " + nuevoEstado);
            }
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        log.info("Eliminando pedido id: {}", id);
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public long contar() {
        return pedidoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEntregasHoyPorRepartidor(Long repartidorId) {
        log.debug("Contando entregas de hoy del repartidor id: {}", repartidorId);
        LocalDate hoy = LocalDate.now();
        return pedidoRepository.findByRepartidorId(repartidorId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .filter(p -> p.getFechaEntrega() != null && p.getFechaEntrega().toLocalDate().equals(hoy))
                .count();
    }
}
