package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.ImpuestoDTOs.GetImpuestoResponse;
import com.backend.backendtienda.repository.ImpuestoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImpuestoService {

    private final ImpuestoRepository impuestoRepository;

    public ImpuestoService(ImpuestoRepository impuestoRepository) {
        this.impuestoRepository = impuestoRepository;
    }

    @Transactional(readOnly = true)
    public List<GetImpuestoResponse> getAll() {
        return impuestoRepository.findAll().stream()
                .map(i -> new GetImpuestoResponse(i.getIdImpuesto(), i.getNombre(), i.getPorcentaje()))
                .toList();
    }
}