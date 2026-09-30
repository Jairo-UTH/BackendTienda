package com.backend.backendtienda.repository;

import com.backend.backendtienda.entity.ProductoMovimiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoMovimientoRepository extends JpaRepository<ProductoMovimiento, Integer> {

    @EntityGraph(attributePaths = {"product", "employee"})
    List<ProductoMovimiento> findAllByOrderByFechaMovimientoDesc();

    @EntityGraph(attributePaths = {"product", "employee"})
    List<ProductoMovimiento> findByProduct_ProductIdOrderByFechaMovimientoDesc(Integer productId);

    @EntityGraph(attributePaths = {"product", "employee"})
    List<ProductoMovimiento> findByEmployee_EmployeeIdOrderByFechaMovimientoDesc(Integer employeeId);

    Optional<ProductoMovimiento> findFirstByProduct_ProductIdAndTipoMovimientoOrderByFechaMovimientoDesc(
    Integer productId, String tipoMovimiento);
}
