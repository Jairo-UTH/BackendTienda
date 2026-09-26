package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.OrderDTOs.CreateOrderRequest;
import com.backend.backendtienda.dto.OrderDTOs.GetOrderListResponse;
import com.backend.backendtienda.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping({"/api/Order", "/api/order"})
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/getAll")
    public ResponseEntity<GetOrderListResponse> getAll() {
    String url = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    return ResponseEntity.ok(new GetOrderListResponse(service.getAll(url)));
    }
}