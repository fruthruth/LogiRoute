package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.*;
import com.logiroute.logiroute.utils.CodigoGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RepartidorRepository repartidorRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Optional<Pedido> obtenerPorCodigo(String codigo) {
        return pedidoRepository.findByCodigo(codigo);
    }

    @Transactional
    public Pedido crear(PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Pedido pedido = Pedido.builder()
                .codigo(CodigoGenerator.generarCodigoPedido())
                .cliente(cliente)
                .direccionOrigen(dto.getDireccionOrigen())
                .direccionDestino(dto.getDireccionDestino())
                .peso(dto.getPeso())
                .tipoPaquete(dto.getTipoPaquete())
                .estado(EstadoPedido.PENDIENTE)
                .build();

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizar(Long id, PedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        pedido.setDireccionOrigen(dto.getDireccionOrigen());
        pedido.setDireccionDestino(dto.getDireccionDestino());
        pedido.setPeso(dto.getPeso());
        pedido.setTipoPaquete(dto.getTipoPaquete());
        pedido.setCliente(cliente);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Optional<Pedido> actualizarEstado(Long id, String estado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstado(EstadoPedido.valueOf(estado));
            return pedidoRepository.save(pedido);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<SeguimientoDTO> seguimiento(String codigo) {
        return pedidoRepository.findByCodigo(codigo).map(pedido -> SeguimientoDTO.builder()
                .pedidoId(pedido.getId())
                .codigo(pedido.getCodigo())
                .estado(pedido.getEstado().name())
                .direccionOrigen(pedido.getDireccionOrigen())
                .direccionDestino(pedido.getDireccionDestino())
                .repartidorNombre(pedido.getRepartidor() != null
                        ? pedido.getRepartidor().getUsuario().getNombre()
                        : null)
                .fechaEstimada(pedido.getFechaEstimada())
                .fechaEntrega(pedido.getFechaEntrega())
                .build());
    }
}
