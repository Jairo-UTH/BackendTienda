package com.backend.backendtienda.controller;

import com.backend.backendtienda.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/Reporte")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/factura/{orderId}")
    public ResponseEntity<byte[]> factura(@PathVariable Integer orderId) {
        return pdfResponse(service.generarFactura(orderId), "factura-" + orderId + ".pdf");
    }

    @GetMapping("/ventas")
    public ResponseEntity<byte[]> ventas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pdfResponse(service.generarReporteVentas(desde, hasta), "reporte-ventas.pdf");
    }

    @GetMapping("/movimientoProducto")
    public ResponseEntity<byte[]> movimientoProducto(@RequestParam Integer productId) {
        return pdfResponse(service.generarReporteMovimientoPorProducto(productId), "reporte-movimiento.pdf");
    }

    @GetMapping("/ventasPorDia")
    public ResponseEntity<byte[]> ventasPorDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pdfResponse(service.generarReporteVentasPorDia(desde, hasta), "reporte-ventas-por-dia.pdf");
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(pdf);
    }
}