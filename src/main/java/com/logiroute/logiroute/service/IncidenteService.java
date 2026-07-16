package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.IncidenteDTO;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Incidente;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.repository.IncidenteRepository;
import com.logiroute.logiroute.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenteService implements IIncidenteService {

    private static final Logger log = LoggerFactory.getLogger(IncidenteService.class);

    private final IncidenteRepository incidenteRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Incidente> listarTodos() {
        return incidenteRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incidente> listarPorPedido(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new RecursoNoEncontradoException("Pedido", pedidoId);
        }
        return incidenteRepository.findByPedidoIdOrderByCreatedAtDesc(pedidoId);
    }

    @Override
    @Transactional
    public Incidente registrar(IncidenteDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del incidente son obligatorios");
        }
        if (dto.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de incidente es obligatorio");
        }
        String descripcion = dto.getDescripcion() == null ? "" : dto.getDescripcion().trim();
        if (descripcion.isEmpty()) {
            throw new IllegalArgumentException("La descripción del incidente es obligatoria");
        }
        if (descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede superar 500 caracteres");
        }

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", dto.getPedidoId()));

        Incidente incidente = Incidente.builder()
                .pedido(pedido)
                .tipo(dto.getTipo())
                .descripcion(descripcion)
                .createdAt(LocalDateTime.now())
                .build();

        Incidente guardado = incidenteRepository.save(incidente);
        log.warn("Incidente registrado: id={}, pedido={}, tipo={}",
                guardado.getId(), pedido.getCodigo(), guardado.getTipo());
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public long contar() {
        return incidenteRepository.count();
    }
}
