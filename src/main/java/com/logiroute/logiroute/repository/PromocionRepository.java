package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByActivaTrue();

    @Query("SELECT p FROM Promocion p WHERE p.activa = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora")
    List<Promocion> findActivasVigentes(@Param("ahora") LocalDateTime ahora);
}
