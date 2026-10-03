package com.backend.backendtienda.repository;

import com.backend.backendtienda.entity.Impuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImpuestoRepository extends JpaRepository<Impuesto, Integer> {
}