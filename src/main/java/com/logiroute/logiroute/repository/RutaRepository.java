package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    List<Ruta> findByActiva(boolean activa);
}
