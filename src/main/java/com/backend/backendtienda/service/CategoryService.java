package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.CategoryDTOs.GetCategoryResponse;
import com.backend.backendtienda.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<GetCategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(c -> new GetCategoryResponse(
                        String.valueOf(c.getCategoryId()),
                        c.getIcon(),
                        c.getName()))
                .toList();
    }
}