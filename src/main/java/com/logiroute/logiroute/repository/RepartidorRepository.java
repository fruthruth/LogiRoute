package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    Optional<Repartidor> findByUsuarioId(Long usuarioId);

    List<Repartidor> findByEstado(Repartidor.EstadoRepartidor estado);

    Optional<Repartidor> findByLicencia(String licencia);
}
