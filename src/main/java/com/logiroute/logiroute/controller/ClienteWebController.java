package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteWebController {

    private final PedidoService pedidoService;

    @GetMapping({"", "/"})
    public String index() {
        return "cliente/seguimiento";
    }

    @GetMapping("/seguimiento")
    public String seguimiento(@RequestParam(required = false) String codigo, Model model) {
        if (codigo != null && !codigo.isEmpty()) {
            model.addAttribute("pedido", pedidoService.seguimiento(codigo).orElse(null));
            model.addAttribute("codigo", codigo);
        }
        return "cliente/seguimiento";
    }
}
