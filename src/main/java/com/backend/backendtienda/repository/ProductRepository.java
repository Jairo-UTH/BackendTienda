package com.backend.backendtienda.repository;

import com.backend.backendtienda.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @EntityGraph(attributePaths = "category")
    List<Product> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "category")
    List<Product> findByCategory_CategoryIdOrderByCreatedAtDesc(Integer categoryId);
}