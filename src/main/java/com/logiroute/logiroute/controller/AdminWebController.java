package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.service.IPedidoService;
import com.logiroute.logiroute.service.IRepartidorService;
import com.logiroute.logiroute.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private static final Logger log = LoggerFactory.getLogger(AdminWebController.class);

    private final IPedidoService pedidoService;
    private final IRepartidorService repartidorService;
    private final IReporteService reporteService;

    @GetMapping({ "", "/" })
    public String dashboard(Model model) {
        log.debug("Renderizando dashboard");
        model.addAttribute("totalPedidos", pedidoService.contar());
        model.addAttribute("totalRepartidores", repartidorService.listarTodos().size());
        return "admin/dashboard";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        log.debug("Renderizando gestión de pedidos");
        model.addAttribute("pedidos", pedidoService.listarTodos());
        model.addAttribute("repartidores", repartidorService.listarTodos());
        return "admin/pedidos";
    }

    @GetMapping("/repartidores")
    public String repartidores(Model model) {
        log.debug("Renderizando gestión de repartidores");
        model.addAttribute("repartidores", repartidorService.listarTodos());
        return "admin/repartidores";
    }

    @GetMapping("/promociones")
    public String promociones() {
        return "admin/promociones";
    }

    @GetMapping("/mapa")
    public String mapa(Model model) {
        model.addAttribute("repartidores", repartidorService.listarTodos());
        return "admin/mapa";
    }

    @GetMapping("/reportes")
    public String reportes(
            @RequestParam(required = false) Integer dia,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) String responsable,
            @RequestParam(required = false) Double costoMin,
            @RequestParam(required = false) Double costoMax,
            Model model) {

        log.debug("Renderizando reportes con filtros");
        List<Pedido> pedidos = reporteService.filtrar(dia, mes, anio, responsable, costoMin, costoMax);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("dia", dia);
        model.addAttribute("mes", mes);
        model.addAttribute("anio", anio);
        model.addAttribute("responsable", responsable);
        model.addAttribute("costoMin", costoMin);
        model.addAttribute("costoMax", costoMax);
        return "admin/reportes";
    }
}
