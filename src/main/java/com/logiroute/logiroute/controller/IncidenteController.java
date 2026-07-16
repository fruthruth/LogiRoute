package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.IncidenteDTO;
import com.logiroute.logiroute.model.Incidente;
import com.logiroute.logiroute.service.IIncidenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/incidentes")
@RequiredArgsConstructor
public class IncidenteController {

    private final IIncidenteService incidenteService;

    @GetMapping
    public List<Incidente> listarTodos() {
        return incidenteService.listarTodos();
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Incidente> listarPorPedido(@PathVariable Long pedidoId) {
        return incidenteService.listarPorPedido(pedidoId);
    }

    @PostMapping
    public ResponseEntity<Incidente> registrar(@Valid @RequestBody IncidenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenteService.registrar(dto));
    }
}
