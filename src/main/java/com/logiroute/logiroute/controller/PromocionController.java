package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.PromocionDTO;
import com.logiroute.logiroute.dto.response.PromocionResponseDTO;
import com.logiroute.logiroute.model.Promocion;
import com.logiroute.logiroute.service.IPromocionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private static final Logger log = LoggerFactory.getLogger(PromocionController.class);

    private final IPromocionService promocionService;

    @GetMapping
    public ResponseEntity<List<PromocionResponseDTO>> listarTodos() {
        log.debug("API: Listando todas las promociones");
        List<PromocionResponseDTO> promociones = promocionService.listarTodos().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(promociones);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<PromocionResponseDTO>> listarActivasVigentes() {
        log.debug("API: Listando promociones activas y vigentes");
        List<PromocionResponseDTO> promociones = promocionService.listarActivasVigentes().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(promociones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.debug("API: Buscando promoción id: {}", id);
        return promocionService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(toResponseDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PromocionResponseDTO> crear(@Valid @RequestBody PromocionDTO dto) {
        log.info("API: Creando promoción: {}", dto.getTitulo());
        Promocion promocion = promocionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(promocion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PromocionDTO dto) {
        log.info("API: Actualizando promoción id: {}", id);
        return promocionService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(toResponseDTO(promocionService.actualizar(id, dto))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PromocionResponseDTO> actualizarEstado(@PathVariable Long id, @RequestParam boolean activa) {
        log.info("API: Actualizando estado de promoción id: {} a {}", id, activa);
        return promocionService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(toResponseDTO(promocionService.actualizarEstado(id, activa))))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("API: Eliminando promoción id: {}", id);
        if (promocionService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private PromocionResponseDTO toResponseDTO(Promocion p) {
        return PromocionResponseDTO.builder()
                .id(p.getId())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .descuentoPorcentaje(p.getDescuentoPorcentaje())
                .montoMinimo(p.getMontoMinimo())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .activa(p.getActiva())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
