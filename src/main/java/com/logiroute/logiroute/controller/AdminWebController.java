package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.service.PedidoService;
import com.logiroute.logiroute.service.RepartidorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final PedidoService pedidoService;
    private final RepartidorService repartidorService;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("totalPedidos", pedidoService.listarTodos().size());
        model.addAttribute("totalRepartidores", repartidorService.listarTodos().size());
        return "admin/dashboard";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "admin/pedidos";
    }

    @GetMapping("/repartidores")
    public String repartidores(Model model) {
        model.addAttribute("repartidores", repartidorService.listarTodos());
        return "admin/repartidores";
    }
}
