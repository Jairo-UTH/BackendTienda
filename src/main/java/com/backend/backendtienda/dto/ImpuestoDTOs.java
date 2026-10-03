package com.backend.backendtienda.dto;

import java.math.BigDecimal;
import java.util.List;

public final class ImpuestoDTOs {

    private ImpuestoDTOs() {
    }

    public record GetImpuestoResponse(Integer idImpuesto, String nombre, BigDecimal porcentaje) {
    }

    public record GetImpuestoListResponse(List<GetImpuestoResponse> impuestos) {
    }
}