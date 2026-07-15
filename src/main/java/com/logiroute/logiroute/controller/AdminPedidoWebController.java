package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.IAsignacionService;
import com.logiroute.logiroute.service.IClienteService;
import com.logiroute.logiroute.service.IPedidoService;
import com.logiroute.logiroute.service.IRepartidorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoWebController {

    private static final Logger log = LoggerFactory.getLogger(AdminPedidoWebController.class);

    private final IPedidoService pedidoService;
    private final IAsignacionService asignacionService;
    private final IClienteService clienteService;
    private final IRepartidorService repartidorService;

    @PostMapping("/crear")
    public String crear(
            @RequestParam Long clienteId,
            @RequestParam String direccionOrigen,
            @RequestParam String direccionDestino,
            @RequestParam BigDecimal peso,
            @RequestParam String tipoPaquete,
            RedirectAttributes ra) {
        log.info("Creando pedido para cliente id: {}", clienteId);
        try {
            PedidoDTO dto = PedidoDTO.builder()
                    .clienteId(clienteId)
                    .direccionOrigen(direccionOrigen)
                    .direccionDestino(direccionDestino)
                    .peso(peso)
                    .tipoPaquete(tipoPaquete)
                    .build();
            pedidoService.crear(dto);
            ra.addFlashAttribute("mensaje", "Pedido creado correctamente");
        } catch (RecursoNoEncontradoException e) {
            log.error("Error al crear pedido: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear pedido: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al crear el pedido");
        }
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        log.debug("Renderizando edición del pedido id: {}", id);
        Pedido pedido = pedidoService.obtenerPorId(id).orElse(null);
        if (pedido == null) {
            ra.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/admin/pedidos";
        }
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteService.listarTodos());
        return "admin/pedido-editar";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam Long clienteId,
            @RequestParam String direccionOrigen,
            @RequestParam String direccionDestino,
            @RequestParam BigDecimal peso,
            @RequestParam String tipoPaquete,
            RedirectAttributes ra) {
        log.info("Actualizando pedido id: {}", id);
        try {
            PedidoDTO dto = PedidoDTO.builder()
                    .clienteId(clienteId)
                    .direccionOrigen(direccionOrigen)
                    .direccionDestino(direccionDestino)
                    .peso(peso)
                    .tipoPaquete(tipoPaquete)
                    .build();
            pedidoService.actualizar(id, dto);
            ra.addFlashAttribute("mensaje", "Pedido actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar pedido: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        log.info("Eliminando pedido id: {}", id);
        try {
            pedidoService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Pedido eliminado correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar pedido: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/asignar")
    public String asignar(
            @RequestParam Long pedidoId,
            @RequestParam Long repartidorId,
            RedirectAttributes ra) {
        log.info("Asignando repartidor {} al pedido {}", repartidorId, pedidoId);
        try {
            AsignacionDTO dto = AsignacionDTO.builder()
                    .pedidoId(pedidoId)
                    .repartidorId(repartidorId)
                    .build();
            asignacionService.asignar(dto);
            ra.addFlashAttribute("mensaje", "Repartidor asignado correctamente");
        } catch (RecursoNoEncontradoException e) {
            log.error("Error al asignar: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al asignar: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al asignar repartidor");
        }
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/{id}/autoasignar")
    public String autoAsignar(@PathVariable Long id, RedirectAttributes ra) {
        log.info("Auto-asignando repartidor al pedido id: {}", id);
        try {
            Pedido pedido = pedidoService.obtenerPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));

            List<Repartidor> disponibles = repartidorService.listarDisponibles();
            if (disponibles.isEmpty()) {
                ra.addFlashAttribute("error", "No hay repartidores disponibles para auto-asignar");
                return "redirect:/admin/pedidos";
            }

            Repartidor repartidor = disponibles.get(0);
            AsignacionDTO dto = AsignacionDTO.builder()
                    .pedidoId(id)
                    .repartidorId(repartidor.getId())
                    .build();
            asignacionService.asignar(dto);
            ra.addFlashAttribute("mensaje", "Repartidor \"" + repartidor.getUsuario().getNombre() + "\" asignado automáticamente");
        } catch (RecursoNoEncontradoException e) {
            log.error("Error en auto-asignación: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en auto-asignación: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al auto-asignar repartidor");
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            RedirectAttributes ra) {
        log.info("Actualizando estado del pedido id: {} a {}", id, estado);
        try {
            pedidoService.actualizarEstado(id, estado);
            ra.addFlashAttribute("mensaje", "Estado actualizado correctamente");
        } catch (EstadoInvalidoException e) {
            log.error("Estado inválido: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al actualizar el estado");
        }
        return "redirect:/admin/pedidos";
    }
}
