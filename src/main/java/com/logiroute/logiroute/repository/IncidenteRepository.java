package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    List<Incidente> findByPedidoId(Long pedidoId);
}
