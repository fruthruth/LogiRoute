package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Incidente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    @EntityGraph(attributePaths = {"pedido", "pedido.cliente", "pedido.cliente.usuario",
            "pedido.repartidor", "pedido.repartidor.usuario"})
    List<Incidente> findByPedidoId(Long pedidoId);

    @EntityGraph(attributePaths = {"pedido", "pedido.cliente", "pedido.cliente.usuario",
            "pedido.repartidor", "pedido.repartidor.usuario"})
    List<Incidente> findByPedidoIdOrderByCreatedAtDesc(Long pedidoId);

    @EntityGraph(attributePaths = {"pedido", "pedido.cliente", "pedido.cliente.usuario",
            "pedido.repartidor", "pedido.repartidor.usuario"})
    List<Incidente> findAllByOrderByCreatedAtDesc();
}
