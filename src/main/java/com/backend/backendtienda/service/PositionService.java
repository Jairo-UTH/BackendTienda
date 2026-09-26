package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.PositionDTOs.GetPosition;
import com.backend.backendtienda.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Transactional(readOnly = true)
    public List<GetPosition> getAll() {
        return positionRepository.findAll().stream()
                .map(p -> new GetPosition(p.getPositionId(), p.getName()))
                .toList();
    }
}