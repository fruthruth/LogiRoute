package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.IAsignacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private static final Logger log = LoggerFactory.getLogger(AsignacionController.class);

    private final IAsignacionService asignacionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> asignar(@Valid @RequestBody AsignacionDTO dto) {
        log.info("API: Asignando repartidor {} al pedido {}", dto.getRepartidorId(), dto.getPedidoId());
        asignacionService.asignar(dto);
        return ResponseEntity.ok(Map.of("mensaje", "Repartidor asignado correctamente"));
    }


    @PostMapping("/auto/{pedidoId}")
    public ResponseEntity<Map<String, Object>> autoAsignar(@PathVariable Long pedidoId) {
        log.info("API: Auto-asignando el pedido {}", pedidoId);
        Repartidor repartidor = asignacionService.autoAsignar(pedidoId);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Repartidor asignado automáticamente",
                "repartidorId", repartidor.getId(),
                "repartidor", repartidor.getUsuario().getNombre()
        ));
    }

    @PostMapping("/completar/{pedidoId}")
    public ResponseEntity<Map<String, String>> completar(@PathVariable Long pedidoId) {
        log.info("API: Completando entrega del pedido {}", pedidoId);
        asignacionService.completarEntrega(pedidoId);
        return ResponseEntity.ok(Map.of("mensaje", "Entrega completada correctamente"));
    }
}
