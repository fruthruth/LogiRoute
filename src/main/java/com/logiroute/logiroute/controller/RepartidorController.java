package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.response.RepartidorResponseDTO;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.IRepartidorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<RepartidorResponseDTO> crear(@RequestBody Map<String, String> request) {
        log.info("API: Creando repartidor con email: {}", request.get("email"));
        Repartidor repartidor = repartidorService.crear(
                request.get("nombre"),
                request.get("email"),
                request.get("password"),
                request.get("telefono"),
                request.get("licencia")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(repartidor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        log.info("API: Actualizando repartidor id: {}", id);
        Repartidor repartidor = repartidorService.actualizar(
                id,
                request.get("nombre"),
                request.get("email"),
                request.get("telefono"),
                request.get("licencia")
        );
        return ResponseEntity.ok(toResponseDTO(repartidor));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<RepartidorResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("API: Actualizando estado del repartidor id: {} a {}", id, estado);
        Repartidor repartidor = repartidorService.actualizarEstado(id, estado);
        return ResponseEntity.ok(toResponseDTO(repartidor));
    }

    @PutMapping("/{id}/ubicacion")
    public ResponseEntity<RepartidorResponseDTO> actualizarUbicacion(
            @PathVariable Long id,
            @RequestBody Map<String, Double> request) {
        log.info("API: Actualizando ubicación del repartidor id: {}", id);
        Repartidor repartidor = repartidorService.actualizarUbicacion(
                id,
                BigDecimal.valueOf(request.get("latitude")),
                BigDecimal.valueOf(request.get("longitude"))
        );
        return ResponseEntity.ok(toResponseDTO(repartidor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("API: Eliminando repartidor id: {}", id);
        repartidorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private RepartidorResponseDTO toResponseDTO(Repartidor r) {
        return RepartidorResponseDTO.builder()
                .id(r.getId())
                .nombre(r.getUsuario().getNombre())
                .email(r.getUsuario().getEmail())
                .telefono(r.getTelefono())
                .licencia(r.getLicencia())
                .estado(r.getEstado().name())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .build();
    }
}
