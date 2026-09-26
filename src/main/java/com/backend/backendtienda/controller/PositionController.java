package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.PositionDTOs.GetPositionListResponse;
import com.backend.backendtienda.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/Position", "/api/position"})
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<GetPositionListResponse> get() {
        return ResponseEntity.ok(new GetPositionListResponse(service.getAll()));
    }
}