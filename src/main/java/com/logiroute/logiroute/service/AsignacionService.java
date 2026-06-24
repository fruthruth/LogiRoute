package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsignacionService implements IAsignacionService {

    private static final Logger log = LoggerFactory.getLogger(AsignacionService.class);

    private final PedidoRepository pedidoRepository;
    private final RepartidorRepository repartidorRepository;
    private final EntregaRepository entregaRepository;

    @Override
    @Transactional
    public void asignar(AsignacionDTO dto) {
        log.info("Asignando repartidor {} al pedido {}", dto.getRepartidorId(), dto.getPedidoId());
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", dto.getPedidoId()));

        Repartidor repartidor = repartidorRepository.findById(dto.getRepartidorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor", dto.getRepartidorId()));

        pedido.setRepartidor(repartidor);
        pedido.setEstado(EstadoPedido.ASIGNADO);
        pedidoRepository.save(pedido);

        Entrega entrega = Entrega.builder()
                .pedido(pedido)
                .repartidor(repartidor)
                .estado(Entrega.EstadoEntrega.PENDIENTE)
                .build();
        entregaRepository.save(entrega);
        log.info("Asignación completada. Entrega creada para pedido {}", dto.getPedidoId());
    }

    @Override
    @Transactional
    public void completarEntrega(Long pedidoId) {
        log.info("Completando entrega del pedido {}", pedidoId);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));

        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);

        entregaRepository.findByPedidoId(pedidoId).ifPresent(entrega -> {
            entrega.setEstado(Entrega.EstadoEntrega.ENTREGADO);
            entregaRepository.save(entrega);
        });
        log.info("Entrega del pedido {} completada", pedidoId);
    }
}
