package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Pedido> obtenerPorCodigo(@PathVariable String codigo) {
        return pedidoService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return pedidoService.actualizarEstado(id, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (pedidoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
