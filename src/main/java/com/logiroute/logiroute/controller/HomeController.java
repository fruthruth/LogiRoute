package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.service.IPromocionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IPromocionService promocionService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("promocionesActivas", promocionService.listarActivasVigentes());
        return "index";
    }
}
