package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.AuthDTOs.LoginRequest;
import com.backend.backendtienda.dto.AuthDTOs.LoginResponse;
import com.backend.backendtienda.entity.Employee;
import com.backend.backendtienda.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest req) {
        Employee employee = employeeRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (employee.getPassword() == null
                || !passwordEncoder.matches(req.password(), employee.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generateToken(employee);

        return new LoginResponse(
                token,
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getPosition().getName());
    }
}