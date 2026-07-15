package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PromocionDTO;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Promocion;
import com.logiroute.logiroute.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromocionService implements IPromocionService {

    private static final Logger log = LoggerFactory.getLogger(PromocionService.class);

    private final PromocionRepository promocionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Promocion> listarTodos() {
        log.debug("Listando todas las promociones");
        return promocionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promocion> listarActivasVigentes() {
        log.debug("Listando promociones activas y vigentes");
        return promocionRepository.findActivasVigentes(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promocion> obtenerPorId(Long id) {
        log.debug("Buscando promoción con id: {}", id);
        return promocionRepository.findById(id);
    }

    @Override
    @Transactional
    public Promocion crear(PromocionDTO dto) {
        log.info("Creando promoción: {}", dto.getTitulo());

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new EstadoInvalidoException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        Promocion promocion = Promocion.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .descuentoPorcentaje(dto.getDescuentoPorcentaje())
                .montoMinimo(dto.getMontoMinimo())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activa(true)
                .build();

        Promocion guardada = promocionRepository.save(promocion);
        log.info("Promoción creada con id: {}", guardada.getId());
        return guardada;
    }

    @Override
    @Transactional
    public Promocion actualizar(Long id, PromocionDTO dto) {
        log.info("Actualizando promoción id: {}", id);
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Promoción", id));

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new EstadoInvalidoException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        promocion.setTitulo(dto.getTitulo());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setDescuentoPorcentaje(dto.getDescuentoPorcentaje());
        promocion.setMontoMinimo(dto.getMontoMinimo());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());

        return promocionRepository.save(promocion);
    }

    @Override
    @Transactional
    public Promocion actualizarEstado(Long id, boolean activa) {
        log.info("Actualizando estado de promoción id: {} a {}", id, activa);
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Promoción", id));

        promocion.setActiva(activa);
        return promocionRepository.save(promocion);
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        log.info("Eliminando promoción id: {}", id);
        if (promocionRepository.existsById(id)) {
            promocionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
