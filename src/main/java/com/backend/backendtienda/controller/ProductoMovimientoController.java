package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.ProductoMovimientoDTOs.CreateMovimientoRequest;
import com.backend.backendtienda.dto.ProductoMovimientoDTOs.GetMovimientoListResponse;
import com.backend.backendtienda.dto.ProductoMovimientoDTOs.GetResumenProductoResponse;
import com.backend.backendtienda.service.ProductoMovimientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/ProductoMovimiento", "/api/productoMovimiento"})
public class ProductoMovimientoController {

    private final ProductoMovimientoService service;

    public ProductoMovimientoController(ProductoMovimientoService service) {
        this.service = service;
    }

    @PostMapping("/compra")
    public ResponseEntity<Integer> registrarCompra(@RequestBody CreateMovimientoRequest request) {
        return ResponseEntity.ok(service.registrarCompra(request));
    }

    @GetMapping("/getAll")
    public ResponseEntity<GetMovimientoListResponse> getAll() {
        return ResponseEntity.ok(new GetMovimientoListResponse(service.getAll()));
    }

    @GetMapping("/byProduct/{productId}")
    public ResponseEntity<GetMovimientoListResponse> getByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(new GetMovimientoListResponse(service.getByProduct(productId)));
    }

    @GetMapping("/byEmployee/{employeeId}")
    public ResponseEntity<GetMovimientoListResponse> getByEmployee(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(new GetMovimientoListResponse(service.getByEmployee(employeeId)));
    }

    @GetMapping("/resumen/{productId}")
    public ResponseEntity<GetResumenProductoResponse> getResumen(@PathVariable Integer productId) {
        return ResponseEntity.ok(service.getResumen(productId));
    }
}