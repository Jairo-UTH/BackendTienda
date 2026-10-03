package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.ProductoMovimientoDTOs.CreateMovimientoRequest;
import com.backend.backendtienda.dto.ProductoMovimientoDTOs.GetMovimientoResponse;
import com.backend.backendtienda.dto.ProductoMovimientoDTOs.GetResumenProductoResponse;
import com.backend.backendtienda.entity.Employee;
import com.backend.backendtienda.entity.Product;
import com.backend.backendtienda.entity.ProductoMovimiento;
import com.backend.backendtienda.repository.EmployeeRepository;
import com.backend.backendtienda.repository.ProductRepository;
import com.backend.backendtienda.repository.ProductoMovimientoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProductoMovimientoService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ProductoMovimientoRepository movimientoRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;

    public ProductoMovimientoService(ProductoMovimientoRepository movimientoRepository,
                                     ProductRepository productRepository,
                                     EmployeeRepository employeeRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
public Integer registrarCompra(CreateMovimientoRequest req) {
    Product product = productRepository.findById(req.productId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no existe"));

    product.setStockQuantity(product.getStockQuantity() + req.cantidad());

    if (req.precioVenta() != null) { 
        product.setPrice(req.precioVenta());
    }

    ProductoMovimiento movimiento = new ProductoMovimiento();
    movimiento.setProduct(product);
    movimiento.setTipoMovimiento("COMPRA");
    movimiento.setCantidad(req.cantidad());
    movimiento.setPrecio(req.precio());
    movimiento.setEmployee(getEmpleadoActual());

    return movimientoRepository.save(movimiento).getIdProductoMovimiento();
}

    @Transactional(readOnly = true)
    public List<GetMovimientoResponse> getAll() {
        return movimientoRepository.findAllByOrderByFechaMovimientoDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetMovimientoResponse> getByProduct(Integer productId) {
        return movimientoRepository.findByProduct_ProductIdOrderByFechaMovimientoDesc(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetMovimientoResponse> getByEmployee(Integer employeeId) {
        return movimientoRepository.findByEmployee_EmployeeIdOrderByFechaMovimientoDesc(employeeId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Employee getEmpleadoActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email).orElse(null);
    }

    private GetMovimientoResponse toResponse(ProductoMovimiento m) {
        return new GetMovimientoResponse(
                m.getIdProductoMovimiento(),
                m.getProduct().getProductId(),
                m.getProduct().getName(),
                m.getTipoMovimiento(),
                m.getCantidad(),
                m.getPrecio(),
                m.getFechaMovimiento().format(DATE_FORMAT),
                m.getEmployee() != null ? m.getEmployee().getEmployeeId() : null,
                m.getEmployee() != null ? m.getEmployee().getFullName() : null);
    }

    @Transactional
    public void registrarVenta(Product product, Integer cantidad, BigDecimal precio, Employee employee) {
        ProductoMovimiento movimiento = new ProductoMovimiento();
        movimiento.setProduct(product);
        movimiento.setTipoMovimiento("VENTA");
        movimiento.setCantidad(cantidad);
        movimiento.setPrecio(precio);
        movimiento.setEmployee(employee);
        movimientoRepository.save(movimiento);
    }

    public GetResumenProductoResponse getResumen(Integer productId) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe"));

    var ultimaCompra = movimientoRepository
            .findFirstByProduct_ProductIdAndTipoMovimientoOrderByFechaMovimientoDesc(productId, "COMPRA");
    var ultimaVenta = movimientoRepository
            .findFirstByProduct_ProductIdAndTipoMovimientoOrderByFechaMovimientoDesc(productId, "VENTA");

    return new GetResumenProductoResponse(
            product.getProductId(),
            product.getName(),
            product.getStockQuantity(),
            product.getPrice(),
            ultimaCompra.map(m -> m.getFechaMovimiento().format(DATE_FORMAT)).orElse(null),
            ultimaCompra.map(ProductoMovimiento::getPrecio).orElse(null),
            ultimaVenta.map(m -> m.getFechaMovimiento().format(DATE_FORMAT)).orElse(null));
}

}