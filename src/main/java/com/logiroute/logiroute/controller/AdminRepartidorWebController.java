package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.IRepartidorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/repartidores")
@RequiredArgsConstructor
public class AdminRepartidorWebController {

    private static final Logger log = LoggerFactory.getLogger(AdminRepartidorWebController.class);

    private final IRepartidorService repartidorService;

    @GetMapping
    public String listar(Model model) {
        log.debug("Renderizando lista de repartidores");
        model.addAttribute("repartidores", repartidorService.listarTodos());
        return "admin/repartidores";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        log.debug("Renderizando edición del repartidor id: {}", id);
        Repartidor repartidor = repartidorService.obtenerPorId(id)
                .orElse(null);
        if (repartidor == null) {
            ra.addFlashAttribute("error", "Repartidor no encontrado");
            return "redirect:/admin/repartidores";
        }
        model.addAttribute("repartidor", repartidor);
        return "admin/repartidor-editar";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String telefono,
            @RequestParam String licencia,
            RedirectAttributes ra) {
        log.info("Creando repartidor: {}", email);
        try {
            repartidorService.crear(nombre, email, password, telefono, licencia);
            ra.addFlashAttribute("mensaje", "Repartidor creado correctamente");
        } catch (Exception e) {
            log.error("Error al crear repartidor: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/repartidores";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String licencia,
            RedirectAttributes ra) {
        log.info("Actualizando repartidor id: {}", id);
        try {
            repartidorService.actualizar(id, nombre, email, telefono, licencia);
            ra.addFlashAttribute("mensaje", "Repartidor actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar repartidor: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/repartidores";
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            RedirectAttributes ra) {
        log.info("Actualizando estado del repartidor id: {} a {}", id, estado);
        try {
            repartidorService.actualizarEstado(id, estado);
            ra.addFlashAttribute("mensaje", "Estado actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/repartidores";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        log.info("Eliminando repartidor id: {}", id);
        try {
            repartidorService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Repartidor eliminado correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar repartidor: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/repartidores";
    }
}
