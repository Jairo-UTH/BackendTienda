package com.backend.backendtienda.dto;

import java.math.BigDecimal;
import java.util.List;

public final class ProductoMovimientoDTOs {

    private ProductoMovimientoDTOs() {
    }

    // ===== requests =====
    public record CreateMovimientoRequest(
        Integer productId,
        String tipoMovimiento,
        Integer cantidad,
        BigDecimal precio,
        BigDecimal precioVenta) {   
}
    // ===== responses =====
    public record GetMovimientoResponse(
            Integer idProductoMovimiento,
            Integer productId,
            String productName,
            String tipoMovimiento,
            Integer cantidad,
            BigDecimal precio,
            String fechaMovimiento,
            Integer employeeId,
            String employeeName) {
    }

    public record GetMovimientoListResponse(List<GetMovimientoResponse> movimientos) {
    }

    public record GetResumenProductoResponse(
        Integer productId,
        String productName,
        Integer existencia,
        BigDecimal precioVenta,
        String fechaUltimaCompra,     
        BigDecimal ultimoPrecioCompra,  
        String fechaUltimaVenta) {      
}
}