package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.EstadoPedido;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.service.IAsignacionService;
import com.logiroute.logiroute.service.IPedidoService;
import com.logiroute.logiroute.service.IRepartidorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/repartidor")
@RequiredArgsConstructor
public class RepartidorWebController {

    private static final Logger log = LoggerFactory.getLogger(RepartidorWebController.class);

    private final IRepartidorService repartidorService;
    private final IPedidoService pedidoService;
    private final IAsignacionService asignacionService;

    @GetMapping({"", "/"})
    public String dashboard(Model model, Authentication auth) {
        log.debug("Renderizando dashboard de repartidor");
        Repartidor repartidor = getRepartidorFromAuth(auth);
        if (repartidor == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidosActivos = pedidoService.listarPorRepartidorId(repartidor.getId()).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGADO && p.getEstado() != EstadoPedido.CANCELADO)
                .toList();

        long entregasHoy = pedidoService.contarEntregasHoyPorRepartidor(repartidor.getId());

        model.addAttribute("repartidor", repartidor);
        model.addAttribute("pedidosActivos", pedidosActivos);
        model.addAttribute("totalActivos", pedidosActivos.size());
        model.addAttribute("entregasHoy", entregasHoy);
        return "repartidor/dashboard";
    }

    @GetMapping("/pedidos")
    public String misPedidos(Model model, Authentication auth) {
        log.debug("Listando pedidos del repartidor");
        Repartidor repartidor = getRepartidorFromAuth(auth);
        if (repartidor == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoService.listarPorRepartidorId(repartidor.getId()).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGADO && p.getEstado() != EstadoPedido.CANCELADO)
                .toList();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("repartidor", repartidor);
        return "repartidor/mis-pedidos";
    }

    @GetMapping("/pedidos/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam String estado,
                                   RedirectAttributes ra, Authentication auth) {
        log.info("Repartidor cambiando estado del pedido {} a {}", id, estado);
        Repartidor repartidor = getRepartidorFromAuth(auth);
        if (repartidor == null) {
            return "redirect:/login";
        }

        try {
            Pedido pedido = pedidoService.obtenerPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));

            if (pedido.getRepartidor() == null || !pedido.getRepartidor().getId().equals(repartidor.getId())) {
                ra.addFlashAttribute("error", "No tienes asignado este pedido");
                return "redirect:/repartidor/pedidos";
            }

            pedidoService.actualizarEstado(id, estado);

            if (estado.equals("ENTREGADO")) {
                asignacionService.completarEntrega(id);
            }

            ra.addFlashAttribute("mensaje", "Estado actualizado correctamente");
        } catch (Exception e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/repartidor/pedidos";
    }

    @PostMapping("/ubicacion")
    public String actualizarUbicacion(@RequestParam BigDecimal latitud, @RequestParam BigDecimal longitud,
                                      RedirectAttributes ra, Authentication auth) {
        log.info("Actualizando ubicación del repartidor: {}, {}", latitud, longitud);
        Repartidor repartidor = getRepartidorFromAuth(auth);
        if (repartidor == null) {
            return "redirect:/login";
        }

        try {
            repartidorService.actualizarUbicacion(repartidor.getId(), latitud, longitud);
            ra.addFlashAttribute("mensaje", "Ubicación actualizada");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar ubicación");
        }
        return "redirect:/repartidor";
    }

    @GetMapping("/historial")
    public String historial(Model model, Authentication auth) {
        log.debug("Listando historial de repartidor");
        Repartidor repartidor = getRepartidorFromAuth(auth);
        if (repartidor == null) {
            return "redirect:/login";
        }

        List<Pedido> historial = pedidoService.listarPorRepartidorId(repartidor.getId()).stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO || p.getEstado() == EstadoPedido.CANCELADO)
                .toList();

        model.addAttribute("pedidos", historial);
        model.addAttribute("repartidor", repartidor);
        return "repartidor/historial";
    }

    private Repartidor getRepartidorFromAuth(Authentication auth) {
        if (auth == null) return null;
        String email = auth.getName();
        return repartidorService.obtenerPorEmail(email).orElse(null);
    }
}
