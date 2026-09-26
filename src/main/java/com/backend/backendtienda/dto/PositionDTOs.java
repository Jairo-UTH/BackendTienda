package com.backend.backendtienda.dto;
import java.util.List;

public final class PositionDTOs {

    private PositionDTOs() {
    }

    public record GetPosition(Integer positionId, String name) {
    }

    public record GetPositionListResponse(List<GetPosition> positions) {
}
}