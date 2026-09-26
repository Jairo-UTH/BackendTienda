package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.OrderDTOs.CreateOrderDetailRequest;
import com.backend.backendtienda.dto.OrderDTOs.CreateOrderRequest;
import com.backend.backendtienda.dto.OrderDTOs.GetOrderDetailResponse;
import com.backend.backendtienda.dto.OrderDTOs.GetOrderResponse;
import com.backend.backendtienda.entity.Order;
import com.backend.backendtienda.entity.OrderDetail;
import com.backend.backendtienda.entity.Product;
import com.backend.backendtienda.repository.OrderRepository;
import com.backend.backendtienda.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final String folder;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        @Value("${app.images.products-folder}") String folder) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.folder = folder;
    }

    @Transactional
    public Integer create(CreateOrderRequest req) {
        if (req.details() == null || req.details().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido no tiene productos");
        }

        Order order = new Order();
        order.setTotalAmount(req.totalAmount());

        for (CreateOrderDetailRequest d : req.details()) {
            Product product = productRepository.findById(d.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El producto " + d.productId() + " no existe"));

            // Descuenta el stock (Hibernate guarda el cambio al terminar la transacción)
            product.setStockQuantity(product.getStockQuantity() - d.quantity());

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(d.quantity());
            detail.setUnitPrice(d.unitPrice());
            order.getOrderDetails().add(detail);
        }

        return orderRepository.save(order).getOrderId();
    }

    @Transactional(readOnly = true)
    public List<GetOrderResponse> getAll(String serverUrl) {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(o -> new GetOrderResponse(
                        o.getOrderId(),
                        o.getOrderDate().format(DATE_FORMAT),
                        o.getTotalAmount(),
                        o.getOrderDetails().stream()
                                .map(od -> new GetOrderDetailResponse(
                                        buildImageUrl(serverUrl, od.getProduct().getImage()),
                                        od.getProduct().getName(),
                                        od.getQuantity(),
                                        od.getUnitPrice().multiply(BigDecimal.valueOf(od.getQuantity()))))
                                .toList()))
                .toList();
    }

    private String buildImageUrl(String serverUrl, String image) {
        if (image == null || image.isBlank()) {
            return null;
        }
        return serverUrl + "/" + folder + "/" + image;
    }
}