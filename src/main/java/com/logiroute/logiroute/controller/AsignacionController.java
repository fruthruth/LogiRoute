package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.service.AsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> asignar(@Valid @RequestBody AsignacionDTO dto) {
        asignacionService.asignar(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Repartidor asignado correctamente"));
    }

    @PostMapping("/completar/{pedidoId}")
    public ResponseEntity<Map<String, String>> completar(@PathVariable Long pedidoId) {
        asignacionService.completarEntrega(pedidoId);
        return ResponseEntity.ok(Map.of("mensaje", "Entrega completada correctamente"));
    }
}
