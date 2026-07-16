package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Override
    @EntityGraph(attributePaths = "usuario")
    List<Cliente> findAll();

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<Cliente> findById(Long id);

    @EntityGraph(attributePaths = "usuario")
    Optional<Cliente> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = "usuario")
    Optional<Cliente> findByUsuarioEmailIgnoreCase(String email);
}
