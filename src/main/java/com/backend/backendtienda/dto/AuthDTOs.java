package com.backend.backendtienda.dto;

public final class AuthDTOs {

    private AuthDTOs() {
    }

    public record LoginRequest(String email, String password) {
    }

    public record LoginResponse(
            String token,
            Integer employeeId,
            String fullName,
            String role) {
    }
}