package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.CategoryDTOs.GetCategoryListResponse;
import com.backend.backendtienda.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/Category", "/api/category"})
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/getAll")
    public ResponseEntity<GetCategoryListResponse> getAll() {
        return ResponseEntity.ok(new GetCategoryListResponse(service.getAll()));
    }
}