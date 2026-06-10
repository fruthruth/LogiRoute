package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.RepartidorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor

public class RepartidorController {

    private final RepartidorService repartidorService;

    @GetMapping
    public ResponseEntity<List<Repartidor>> listarTodos() {
        return ResponseEntity.ok(repartidorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repartidor> obtenerPorId(@PathVariable Long id) {
        return repartidorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Repartidor>> listarDisponibles() {
        return ResponseEntity.ok(repartidorService.listarDisponibles());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Repartidor> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return repartidorService.actualizarEstado(id, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
