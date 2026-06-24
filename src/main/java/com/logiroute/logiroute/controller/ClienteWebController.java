package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteWebController {

    private static final Logger log = LoggerFactory.getLogger(ClienteWebController.class);

    private final IReporteService reporteService;

    @GetMapping({"", "/"})
    public String index() {
        log.debug("Renderizando página de seguimiento");
        return "cliente/seguimiento";
    }

    @GetMapping("/seguimiento")
    public String seguimiento(@RequestParam(required = false) String codigo, Model model) {
        if (codigo != null && !codigo.isEmpty()) {
            log.info("Buscando seguimiento del código: {}", codigo);
            SeguimientoDTO dto = reporteService.seguimiento(codigo).orElse(null);
            model.addAttribute("pedido", dto);
            model.addAttribute("codigo", codigo);
        }
        return "cliente/seguimiento";
    }
}
