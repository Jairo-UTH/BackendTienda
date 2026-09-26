package com.backend.backendtienda.controller;

import com.backend.backendtienda.dto.EmployeeDTOs.CreateEmployee;
import com.backend.backendtienda.dto.EmployeeDTOs.GetEmployee;
import com.backend.backendtienda.dto.EmployeeDTOs.GetEmployeeListResponse;
import com.backend.backendtienda.dto.EmployeeDTOs.UpdateEmployee;
import com.backend.backendtienda.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/Employee", "/api/employee"})
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<GetEmployeeListResponse> get() {
        return ResponseEntity.ok(new GetEmployeeListResponse(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetEmployee> get(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Void> post(@RequestBody CreateEmployee request) {
        service.add(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> put(@RequestBody UpdateEmployee request) {
        service.update(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}