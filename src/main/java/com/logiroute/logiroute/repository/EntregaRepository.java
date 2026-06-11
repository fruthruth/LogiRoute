package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByPedidoId(Long pedidoId);

    List<Entrega> findByRepartidorId(Long repartidorId);

    List<Entrega> findByEstado(Entrega.EstadoEntrega estado);
}
