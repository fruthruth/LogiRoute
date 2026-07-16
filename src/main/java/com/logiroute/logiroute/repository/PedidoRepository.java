package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.EstadoPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    String[] DETALLE = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"};

    @Override
    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    List<Pedido> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    Optional<Pedido> findById(Long id);

    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    Optional<Pedido> findByCodigo(String codigo);

    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    List<Pedido> findByClienteId(Long clienteId);

    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    List<Pedido> findByRepartidorId(Long repartidorId);

    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    List<Pedido> findByRepartidorIdAndEstadoIn(Long repartidorId, Collection<EstadoPedido> estados);

    @EntityGraph(attributePaths = {"cliente", "cliente.usuario", "repartidor", "repartidor.usuario", "ruta"})
    List<Pedido> findByEstado(EstadoPedido estado);
}
