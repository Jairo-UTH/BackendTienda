package com.backend.backendtienda.dto;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public final class ProductDTOs {

    private ProductDTOs() {
    }

    // ===== requests =====
        public record CreateProductRequest(
                Integer categoryId,
                String name,
                BigDecimal price,
                Integer stockQuantity,
                Integer idImpuesto,         
                MultipartFile image) {
        }

        public record UpdateProductRequest(
                Integer productId,
                Integer categoryId,
                String name,
                BigDecimal price,
                Integer stockQuantity,
                Integer idImpuesto,        
                MultipartFile image) {
        }

        public record GetProductResponse(
                Integer productId,
                String categoryId,
                String categoryName,
                String name,
                BigDecimal price,
                Integer stockQuantity,
                Integer idImpuesto,          
                String impuestoNombre,     
                BigDecimal impuestoPorcentaje, 
                String imageUrl) {
        }

    public record ReturnProductRequest(String categoryId) {
    }

    // ===== responses =====
   

    public record GetProductListResponse(List<GetProductResponse> products) {
        }

}