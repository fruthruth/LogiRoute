package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.dto.PedidoMapper;
import com.logiroute.logiroute.dto.response.PedidoResponseDTO;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.service.IPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    private final IPedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        log.debug("API: Listando todos los pedidos");
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos().stream()
                .map(PedidoMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.debug("API: Buscando pedido id: {}", id);
        return pedidoService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(PedidoMapper.toResponseDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        log.debug("API: Buscando pedido código: {}", codigo);
        return pedidoService.obtenerPorCodigo(codigo)
                .map(p -> ResponseEntity.ok(PedidoMapper.toResponseDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoDTO dto) {
        log.info("API: Creando nuevo pedido");
        Pedido pedido = pedidoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoMapper.toResponseDTO(pedido));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("API: Actualizando estado del pedido id: {} a {}", id, estado);
        return pedidoService.actualizarEstado(id, estado)
                .map(p -> ResponseEntity.ok(PedidoMapper.toResponseDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("API: Eliminando pedido id: {}", id);
        if (pedidoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
