package com.backend.backendtienda.repository;

import com.backend.backendtienda.entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Override
    @EntityGraph(attributePaths = "position")
    List<Employee> findAll();
    Optional<Employee> findByEmail(String email);
    @Override
    @EntityGraph(attributePaths = "position")
    Optional<Employee> findById(Integer id);
}