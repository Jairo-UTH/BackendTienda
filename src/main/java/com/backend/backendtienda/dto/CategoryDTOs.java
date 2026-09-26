package com.backend.backendtienda.dto;
import java.util.List;

public final class CategoryDTOs {

    private CategoryDTOs() {
    }

    // ===== responses =====
    public record GetCategoryResponse(String categoryId, String icon, String name) {
    }

    public record GetCategoryListResponse(List<GetCategoryResponse> categories) {
    }
}