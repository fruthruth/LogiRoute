package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Repartidor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    @Override
    @EntityGraph(attributePaths = "usuario")
    List<Repartidor> findAll();

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<Repartidor> findById(Long id);

    @EntityGraph(attributePaths = "usuario")
    Optional<Repartidor> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = "usuario")
    List<Repartidor> findByEstado(Repartidor.EstadoRepartidor estado);

    @EntityGraph(attributePaths = "usuario")
    Optional<Repartidor> findByLicencia(String licencia);

    @EntityGraph(attributePaths = "usuario")
    Optional<Repartidor> findByUsuarioEmailIgnoreCase(String email);
}
