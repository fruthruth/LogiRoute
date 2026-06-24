package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.service.IReporteService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) Integer dia,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) String responsable,
            @RequestParam(required = false) Double costoMin,
            @RequestParam(required = false) Double costoMax) {

        log.info("API: Exportando reporte a Excel");

        List<com.logiroute.logiroute.model.Pedido> pedidos =
                reporteService.filtrar(dia, mes, anio, responsable, costoMin, costoMax);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Cliente", "Repartidor", "Origen", "Destino", "Estado", "Costo", "Fecha"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (com.logiroute.logiroute.model.Pedido p : pedidos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(StringUtils.defaultString(p.getCodigo()));
                row.createCell(1).setCellValue(p.getCliente() != null ? p.getCliente().getUsuario().getNombre() : "");
                row.createCell(2).setCellValue(p.getRepartidor() != null ? p.getRepartidor().getUsuario().getNombre() : "");
                row.createCell(3).setCellValue(StringUtils.defaultString(p.getDireccionOrigen()));
                row.createCell(4).setCellValue(StringUtils.defaultString(p.getDireccionDestino()));
                row.createCell(5).setCellValue(p.getEstado().name());
                row.createCell(6).setCellValue(p.getCosto() != null ? p.getCosto().doubleValue() : 0);
                row.createCell(7).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }

            workbook.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());

        } catch (IOException e) {
            log.error("Error al generar Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
