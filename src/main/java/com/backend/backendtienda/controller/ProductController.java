package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.ProductDTOs.CreateProductRequest;
import com.backend.backendtienda.dto.ProductDTOs.GetProductListResponse;
import com.backend.backendtienda.dto.ProductDTOs.GetProductResponse;
import com.backend.backendtienda.dto.ProductDTOs.ReturnProductRequest;
import com.backend.backendtienda.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;
import com.backend.backendtienda.dto.ProductDTOs.UpdateProductRequest;

@RestController
@RequestMapping({"/api/Product", "/api/product"})
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Integer> create(@ModelAttribute CreateProductRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/getAll")
    public ResponseEntity<GetProductListResponse> getAll(@ModelAttribute ReturnProductRequest request) {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return ResponseEntity.ok(new GetProductListResponse(service.getAll(url, request.categoryId())));
    }

   @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@ModelAttribute UpdateProductRequest request) {
    service.update(request);
    return ResponseEntity.ok().build();
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}