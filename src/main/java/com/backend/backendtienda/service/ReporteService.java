package com.backend.backendtienda.service;

import com.backend.backendtienda.entity.Order;
import com.backend.backendtienda.entity.OrderDetail;
import com.backend.backendtienda.entity.Product;
import com.backend.backendtienda.entity.ProductoMovimiento;
import com.backend.backendtienda.repository.OrderRepository;
import com.backend.backendtienda.repository.ProductRepository;
import com.backend.backendtienda.repository.ProductoMovimientoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final OrderRepository orderRepository;
    private final ProductoMovimientoRepository movimientoRepository;
    private final ProductRepository productRepository;

    public ReporteService(OrderRepository orderRepository,
                          ProductoMovimientoRepository movimientoRepository,
                          ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.movimientoRepository = movimientoRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generarFactura(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La factura no existe"));

        List<Map<String, Object>> detalle = new ArrayList<>();
        for (OrderDetail d : order.getOrderDetails()) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("productName", d.getProduct().getName());
            fila.put("quantity", d.getQuantity());
            fila.put("unitPrice", d.getUnitPrice());
            fila.put("subtotal", d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())));
            fila.put("impuesto", d.getImpuesto());
            detalle.add(fila);
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("orderId", order.getOrderId());
        parametros.put("fecha", order.getOrderDate().format(DATE_FORMAT));
        parametros.put("empleado", order.getEmployee() != null ? order.getEmployee().getFullName() : null);
        parametros.put("totalAmount", order.getTotalAmount());
        parametros.put("totalImpuesto", order.getTotalImpuesto());
        parametros.put("granTotal", order.getTotalAmount().add(order.getTotalImpuesto()));

        return compilarYExportar("reports/factura.jrxml", parametros, detalle);
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteVentas(LocalDate desde, LocalDate hasta) {
        List<Order> orders = new ArrayList<>();
        for (Order o : orderRepository.findAllByOrderByOrderDateDesc()) {
            if (dentroDelRango(o.getOrderDate().toLocalDate(), desde, hasta)) {
                orders.add(o);
            }
        }

        List<Map<String, Object>> filas = new ArrayList<>();
        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalImpuesto = BigDecimal.ZERO;

        for (Order order : orders) {
            for (OrderDetail d : order.getOrderDetails()) {
                BigDecimal subtotal = d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity()));

                Map<String, Object> fila = new HashMap<>();
                fila.put("fecha", order.getOrderDate().format(DATE_FORMAT));
                fila.put("orderId", order.getOrderId());
                fila.put("productName", d.getProduct().getName());
                fila.put("quantity", d.getQuantity());
                fila.put("subtotal", subtotal);
                fila.put("impuesto", d.getImpuesto());
                fila.put("empleado", order.getEmployee() != null ? order.getEmployee().getFullName() : "—");
                filas.add(fila);

                totalVentas = totalVentas.add(subtotal);
                totalImpuesto = totalImpuesto.add(d.getImpuesto());
            }
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("totalVentas", totalVentas);
        parametros.put("totalImpuesto", totalImpuesto);

        return compilarYExportar("reports/reporteVentas.jrxml", parametros, filas);
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteMovimientoPorProducto(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no existe"));

        List<ProductoMovimiento> movimientos =
                movimientoRepository.findByProduct_ProductIdOrderByFechaMovimientoDesc(productId);

        List<Map<String, Object>> filas = new ArrayList<>();
        for (ProductoMovimiento m : movimientos) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", m.getFechaMovimiento().format(DATE_FORMAT));
            fila.put("tipoMovimiento", m.getTipoMovimiento());
            fila.put("cantidad", m.getCantidad());
            fila.put("precio", m.getPrecio());
            fila.put("empleado", m.getEmployee() != null ? m.getEmployee().getFullName() : "—");
            filas.add(fila);
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("productoNombre", product.getName());

        return compilarYExportar("reports/reporteMovimiento.jrxml", parametros, filas);
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteVentasPorDia(LocalDate desde, LocalDate hasta) {
        List<Order> orders = new ArrayList<>();
        for (Order o : orderRepository.findAllByOrderByOrderDateDesc()) {
            if (dentroDelRango(o.getOrderDate().toLocalDate(), desde, hasta)) {
                orders.add(o);
            }
        }

        Map<LocalDate, List<Order>> porDia = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderDate().toLocalDate()));

        List<LocalDate> fechasOrdenadas = new ArrayList<>(porDia.keySet());
        fechasOrdenadas.sort(Comparator.reverseOrder());

        List<Map<String, Object>> filas = new ArrayList<>();
        for (LocalDate fecha : fechasOrdenadas) {
            List<Order> ordersDelDia = porDia.get(fecha);

            BigDecimal totalDia = BigDecimal.ZERO;
            BigDecimal impuestoDia = BigDecimal.ZERO;
            for (Order o : ordersDelDia) {
                totalDia = totalDia.add(o.getTotalAmount());
                impuestoDia = impuestoDia.add(o.getTotalImpuesto());
            }

            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", fecha.format(DAY_FORMAT));
            fila.put("cantidadFacturas", ordersDelDia.size());
            fila.put("totalVentas", totalDia);
            fila.put("totalImpuesto", impuestoDia);
            filas.add(fila);
        }

        return compilarYExportar("reports/reporteVentasPorDia.jrxml", new HashMap<>(), filas);
    }

    private boolean dentroDelRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        if (desde != null && fecha.isBefore(desde)) return false;
        if (hasta != null && fecha.isAfter(hasta)) return false;
        return true;
    }

    private byte[] compilarYExportar(String rutaPlantilla, Map<String, Object> parametros, List<Map<String, Object>> datos) {
    try (InputStream plantilla = new ClassPathResource(rutaPlantilla).getInputStream()) {
        JasperReport reporte = JasperCompileManager.compileReport(plantilla);
        JRDataSource dataSource = new JRMapCollectionDataSource((Collection) datos);
        JasperPrint print = JasperFillManager.fillReport(reporte, parametros, dataSource);
        return JasperExportManager.exportReportToPdf(print);
    } catch (Exception e) {
        e.printStackTrace();  
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "No se pudo generar el reporte: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
    }
}
    
}