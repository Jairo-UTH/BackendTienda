package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.ImpuestoDTOs.GetImpuestoListResponse;
import com.backend.backendtienda.service.ImpuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/Impuesto", "/api/impuesto"})
public class ImpuestoController {

    private final ImpuestoService service;

    public ImpuestoController(ImpuestoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<GetImpuestoListResponse> get() {
        return ResponseEntity.ok(new GetImpuestoListResponse(service.getAll()));
    }
}