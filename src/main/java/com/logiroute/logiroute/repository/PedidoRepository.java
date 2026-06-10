package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByCodigo(String codigo);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByRepartidorId(Long repartidorId);

    List<Pedido> findByEstado(EstadoPedido estado);
}
