package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicSeguimientoController {

    private final IReporteService reporteService;

    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<SeguimientoDTO> buscarPorCodigo(@PathVariable String codigo) {
        return reporteService.seguimiento(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
