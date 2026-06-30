package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.PromocionDTO;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Promocion;
import com.logiroute.logiroute.service.IPromocionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/promociones")
@RequiredArgsConstructor
public class AdminPromocionWebController {

    private static final Logger log = LoggerFactory.getLogger(AdminPromocionWebController.class);

    private final IPromocionService promocionService;

    @GetMapping
    public String listar(Model model) {
        log.debug("Renderizando lista de promociones");
        model.addAttribute("promociones", promocionService.listarTodos());
        return "admin/promociones";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        log.debug("Renderizando edición de promoción id: {}", id);
        Promocion promocion = promocionService.obtenerPorId(id).orElse(null);
        if (promocion == null) {
            ra.addFlashAttribute("error", "Promoción no encontrada");
            return "redirect:/admin/promociones";
        }
        model.addAttribute("promocion", promocion);
        return "admin/promocion-editar";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam Double descuentoPorcentaje,
            @RequestParam(required = false) Double montoMinimo,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            RedirectAttributes ra) {
        log.info("Creando promoción: {}", titulo);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            PromocionDTO dto = PromocionDTO.builder()
                    .titulo(titulo)
                    .descripcion(descripcion)
                    .descuentoPorcentaje(new java.math.BigDecimal(descuentoPorcentaje))
                    .montoMinimo(montoMinimo != null ? new java.math.BigDecimal(montoMinimo) : null)
                    .fechaInicio(LocalDateTime.parse(fechaInicio, formatter))
                    .fechaFin(LocalDateTime.parse(fechaFin, formatter))
                    .build();
            promocionService.crear(dto);
            ra.addFlashAttribute("mensaje", "Promoción creada correctamente");
        } catch (EstadoInvalidoException e) {
            log.error("Error al crear promoción: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear promoción: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al crear la promoción");
        }
        return "redirect:/admin/promociones";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam Double descuentoPorcentaje,
            @RequestParam(required = false) Double montoMinimo,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            RedirectAttributes ra) {
        log.info("Actualizando promoción id: {}", id);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            PromocionDTO dto = PromocionDTO.builder()
                    .titulo(titulo)
                    .descripcion(descripcion)
                    .descuentoPorcentaje(new java.math.BigDecimal(descuentoPorcentaje))
                    .montoMinimo(montoMinimo != null ? new java.math.BigDecimal(montoMinimo) : null)
                    .fechaInicio(LocalDateTime.parse(fechaInicio, formatter))
                    .fechaFin(LocalDateTime.parse(fechaFin, formatter))
                    .build();
            promocionService.actualizar(id, dto);
            ra.addFlashAttribute("mensaje", "Promoción actualizada correctamente");
        } catch (EstadoInvalidoException e) {
            log.error("Error al actualizar promoción: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar promoción: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al actualizar la promoción");
        }
        return "redirect:/admin/promociones";
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam boolean activa,
            RedirectAttributes ra) {
        log.info("Actualizando estado de promoción id: {} a {}", id, activa);
        try {
            promocionService.actualizarEstado(id, activa);
            ra.addFlashAttribute("mensaje", "Estado actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promociones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        log.info("Eliminando promoción id: {}", id);
        try {
            promocionService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Promoción eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar promoción: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promociones";
    }
}
