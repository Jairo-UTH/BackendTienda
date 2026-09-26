package com.backend.backendtienda.dto;

import java.time.LocalDate;
import java.util.List;

public final class EmployeeDTOs {

    private EmployeeDTOs() {
    }

    // ===== responses =====
    public record GetEmployee(
            Integer employeeId,
            String fullName,
            String email,
            LocalDate birthDate,
            Integer positionId,
            String position) {
    }

    // ===== requests =====
    public record CreateEmployee(
            String fullName,
            String email,
            LocalDate birthDate,
            Integer positionId,
            String password) {   
    }

    public record UpdateEmployee(
        Integer employeeId,
        String fullName,
        String email,
        LocalDate birthDate,
        Integer positionId,
        String password) {   
}

        public record GetEmployeeListResponse(List<GetEmployee> employees) {
}
}