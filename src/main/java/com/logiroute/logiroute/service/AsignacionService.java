package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final PedidoRepository pedidoRepository;
    private final RepartidorRepository repartidorRepository;
    private final EntregaRepository entregaRepository;

    @Transactional
    public void asignar(AsignacionDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Repartidor repartidor = repartidorRepository.findById(dto.getRepartidorId())
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        pedido.setRepartidor(repartidor);
        pedido.setEstado(EstadoPedido.ASIGNADO);
        pedidoRepository.save(pedido);

        Entrega entrega = Entrega.builder()
                .pedido(pedido)
                .repartidor(repartidor)
                .estado(Entrega.EstadoEntrega.PENDIENTE)
                .build();
        entregaRepository.save(entrega);
    }

    @Transactional
    public void completarEntrega(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);

        entregaRepository.findByPedidoId(pedidoId).ifPresent(entrega -> {
            entrega.setEstado(Entrega.EstadoEntrega.ENTREGADO);
            entregaRepository.save(entrega);
        });
    }
}
