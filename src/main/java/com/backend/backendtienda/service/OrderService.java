package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.OrderDTOs.CreateOrderDetailRequest;
import com.backend.backendtienda.dto.OrderDTOs.CreateOrderRequest;
import com.backend.backendtienda.dto.OrderDTOs.GetOrderDetailResponse;
import com.backend.backendtienda.dto.OrderDTOs.GetOrderResponse;
import com.backend.backendtienda.entity.Employee;
import com.backend.backendtienda.entity.Order;
import com.backend.backendtienda.entity.OrderDetail;
import com.backend.backendtienda.entity.Product;
import com.backend.backendtienda.repository.EmployeeRepository;
import com.backend.backendtienda.repository.OrderRepository;
import com.backend.backendtienda.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmployeeRepository employeeRepository;
    private final ProductoMovimientoService movimientoService;
    private final String folder;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        EmployeeRepository employeeRepository,
                        ProductoMovimientoService movimientoService,
                        @Value("${app.images.products-folder}") String folder) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.movimientoService = movimientoService;
        this.folder = folder;
    }

    @Transactional
    public Integer create(CreateOrderRequest req) {
        if (req.details() == null || req.details().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido no tiene productos");
        }

        Employee empleadoActual = getEmpleadoActualOpcional();

        Order order = new Order();
        order.setTotalAmount(req.totalAmount());
        order.setEmployee(empleadoActual);

        for (CreateOrderDetailRequest d : req.details()) {
            Product product = productRepository.findById(d.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "El producto " + d.productId() + " no existe"));

            product.setStockQuantity(product.getStockQuantity() - d.quantity());

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(d.quantity());
            detail.setUnitPrice(d.unitPrice());
            order.getOrderDetails().add(detail);

            movimientoService.registrarVenta(product, d.quantity(), d.unitPrice(), empleadoActual);
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
                        o.getEmployee() != null ? o.getEmployee().getFullName() : null,
                        o.getOrderDetails().stream()
                                .map(od -> new GetOrderDetailResponse(
                                        buildImageUrl(serverUrl, od.getProduct().getImage()),
                                        od.getProduct().getName(),
                                        od.getQuantity(),
                                        od.getUnitPrice().multiply(BigDecimal.valueOf(od.getQuantity()))))
                                .toList()))
                .toList();
    }

    private Employee getEmpleadoActualOpcional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return employeeRepository.findByEmail(auth.getName()).orElse(null);
    }

    private String buildImageUrl(String serverUrl, String image) {
        if (image == null || image.isBlank()) {
            return null;
        }
        return serverUrl + "/" + folder + "/" + image;
    }
}