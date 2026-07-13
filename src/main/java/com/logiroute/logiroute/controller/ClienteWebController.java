package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.ClienteDTO;
import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.model.Cliente;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.service.IClienteService;
import com.logiroute.logiroute.service.IPedidoService;
import com.logiroute.logiroute.service.IReporteService;
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
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteWebController {

    private static final Logger log = LoggerFactory.getLogger(ClienteWebController.class);

    private final IClienteService clienteService;
    private final IReporteService reporteService;
    private final IPedidoService pedidoService;

    @GetMapping({"", "/"})
    public String index(Model model, Authentication auth) {
        log.debug("Renderizando página de cliente");
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoService.listarTodos().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId().equals(cliente.getId()))
                .toList();

        List<Pedido> recientes = pedidos.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .toList();

        model.addAttribute("cliente", cliente);
        model.addAttribute("pedidosCount", pedidos.size());
        model.addAttribute("pedidosRecientes", recientes);
        return "cliente/index";
    }

    @GetMapping("/seguimiento")
    public String seguimiento(@RequestParam(required = false) String codigo, Model model) {
        if (codigo != null && !codigo.isEmpty()) {
            log.info("Buscando seguimiento del código: {}", codigo);
            model.addAttribute("pedido", reporteService.seguimiento(codigo).orElse(null));
            model.addAttribute("codigo", codigo);
        }
        return "cliente/seguimiento";
    }

    @GetMapping("/pedidos")
    public String misPedidos(Model model, Authentication auth) {
        log.debug("Listando pedidos del cliente");
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoService.listarTodos().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId().equals(cliente.getId()))
                .toList();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);
        return "cliente/pedidos";
    }

    @GetMapping("/pedidos/{id}")
    public String pedidoDetalle(@PathVariable Long id, Model model, Authentication auth) {
        log.debug("Mostrando detalle del pedido {}", id);
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }

        Pedido pedido = pedidoService.obtenerPorId(id).orElse(null);
        if (pedido == null || pedido.getCliente() == null || !pedido.getCliente().getId().equals(cliente.getId())) {
            return "redirect:/cliente/pedidos";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("cliente", cliente);
        return "cliente/pedido-detalle";
    }

    @GetMapping("/pedidos/crear")
    public String formularioCrear(Model model, Authentication auth) {
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }
        model.addAttribute("cliente", cliente);
        return "cliente/pedido-crear";
    }

    @PostMapping("/pedidos/crear")
    public String crearPedido(
            @RequestParam String direccionOrigen,
            @RequestParam String direccionDestino,
            @RequestParam BigDecimal peso,
            @RequestParam String tipoPaquete,
            RedirectAttributes ra,
            Authentication auth) {
        log.info("Cliente creando nuevo pedido");
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }

        try {
            PedidoDTO dto = PedidoDTO.builder()
                    .clienteId(cliente.getId())
                    .direccionOrigen(direccionOrigen)
                    .direccionDestino(direccionDestino)
                    .peso(peso)
                    .tipoPaquete(tipoPaquete)
                    .build();
            pedidoService.crear(dto);
            ra.addFlashAttribute("mensaje", "Pedido creado correctamente");
        } catch (Exception e) {
            log.error("Error al crear pedido: {}", e.getMessage());
            ra.addFlashAttribute("error", "Error al crear el pedido: " + e.getMessage());
        }
        return "redirect:/cliente/pedidos";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }
        model.addAttribute("cliente", cliente);
        return "cliente/perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam String telefono,
            @RequestParam String direccion,
            RedirectAttributes ra,
            Authentication auth) {
        log.info("Actualizando perfil del cliente");
        Cliente cliente = getClienteFromAuth(auth);
        if (cliente == null) {
            return "redirect:/login";
        }

        try {
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            clienteService.actualizar(cliente.getId(), ClienteDTO.builder()
                    .telefono(telefono)
                    .direccion(direccion)
                    .build());
            ra.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el perfil");
        }
        return "redirect:/cliente/perfil";
    }

    private Cliente getClienteFromAuth(Authentication auth) {
        if (auth == null) return null;
        String email = auth.getName();
        return clienteService.listarTodos().stream()
                .filter(c -> c.getUsuario().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
}
