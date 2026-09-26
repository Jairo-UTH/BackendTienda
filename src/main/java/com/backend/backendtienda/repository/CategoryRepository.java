package com.backend.backendtienda.repository;

import com.backend.backendtienda.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}