package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.response.RepartidorResponseDTO;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.IRepartidorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    private static final Logger log = LoggerFactory.getLogger(RepartidorController.class);

    private final IRepartidorService repartidorService;

    @GetMapping
    public ResponseEntity<List<RepartidorResponseDTO>> listarTodos() {
        log.debug("API: Listando todos los repartidores");
        List<RepartidorResponseDTO> repartidores = repartidorService.listarTodos().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.debug("API: Buscando repartidor id: {}", id);
        return repartidorService.obtenerPorId(id)
                .map(r -> ResponseEntity.ok(toResponseDTO(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<RepartidorResponseDTO>> listarDisponibles() {
        log.debug("API: Listando repartidores disponibles");
        List<RepartidorResponseDTO> disponibles = repartidorService.listarDisponibles().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(disponibles);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<RepartidorResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("API: Actualizando estado del repartidor id: {} a {}", id, estado);
        return repartidorService.actualizarEstado(id, estado)
                .map(r -> ResponseEntity.ok(toResponseDTO(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    private RepartidorResponseDTO toResponseDTO(Repartidor r) {
        return RepartidorResponseDTO.builder()
                .id(r.getId())
                .nombre(r.getUsuario().getNombre())
                .email(r.getUsuario().getEmail())
                .telefono(r.getTelefono())
                .licencia(r.getLicencia())
                .estado(r.getEstado().name())
                .build();
    }
}
