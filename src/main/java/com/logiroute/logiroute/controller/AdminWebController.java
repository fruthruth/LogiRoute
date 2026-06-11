package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.service.PedidoService;
import com.logiroute.logiroute.service.RepartidorService;
import lombok.RequiredArgsConstructor;
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

    private final PedidoService pedidoService;
    private final RepartidorService repartidorService;

    @GetMapping({ "", "/" })
    public String dashboard(Model model) {
        model.addAttribute("totalPedidos", pedidoService.listarTodos().size());
        model.addAttribute("totalRepartidores", repartidorService.listarTodos().size());
        return "admin/dashboard";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        model.addAttribute("repartidores", repartidorService.listarTodos());
        return "admin/pedidos";
    }

    @GetMapping("/repartidores")
    public String repartidores(Model model) {
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

        List<Pedido> pedidos = pedidoService.listarTodos().stream()
                .filter(p -> dia == null || p.getCreatedAt().getDayOfMonth() == dia)
                .filter(p -> mes == null || p.getCreatedAt().getMonthValue() == mes)
                .filter(p -> anio == null || p.getCreatedAt().getYear() == anio)
                .filter(p -> responsable == null || responsable.isBlank() ||
                        (p.getRepartidor() != null &&
                                p.getRepartidor().getUsuario().getNombre()
                                        .toLowerCase().contains(responsable.toLowerCase())))
                .filter(p -> costoMin == null || (p.getCosto() != null &&
                        p.getCosto().doubleValue() >= costoMin))
                .filter(p -> costoMax == null || (p.getCosto() != null &&
                        p.getCosto().doubleValue() <= costoMax))
                .collect(java.util.stream.Collectors.toList());

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
