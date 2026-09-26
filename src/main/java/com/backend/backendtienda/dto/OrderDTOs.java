package com.backend.backendtienda.dto;

import java.math.BigDecimal;
import java.util.List;

public final class OrderDTOs {

    private OrderDTOs() {
    }

    // ===== requests =====
    public record CreateOrderRequest(
            BigDecimal totalAmount,
            List<CreateOrderDetailRequest> details) {
    }

    public record CreateOrderDetailRequest(
            Integer productId,
            Integer quantity,
            BigDecimal unitPrice) {
    }

    // ===== responses =====
    public record GetOrderResponse(
            Integer orderId,
            String date,                      
            BigDecimal totalAmount,
            List<GetOrderDetailResponse> details) {
    }

    public record GetOrderDetailResponse(
            String imageUrl,
            String productName,
            Integer quantity,
            BigDecimal total) {               
    }

    public record GetOrderListResponse(List<GetOrderResponse> orders) {
}
}