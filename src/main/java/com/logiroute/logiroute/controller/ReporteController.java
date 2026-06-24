package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    private final IReporteService reporteService;

    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<SeguimientoDTO> seguimiento(@PathVariable String codigo) {
        log.debug("API: Buscando seguimiento del código: {}", codigo);
        return reporteService.seguimiento(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
