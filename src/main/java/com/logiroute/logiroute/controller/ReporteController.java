package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor

public class ReporteController {

    private final PedidoService pedidoService;

    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<SeguimientoDTO> seguimiento(@PathVariable String codigo) {
        return pedidoService.seguimiento(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
