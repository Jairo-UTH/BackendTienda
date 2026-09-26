package com.backend.backendtienda.service;

import com.backend.backendtienda.dto.ProductDTOs.CreateProductRequest;
import com.backend.backendtienda.dto.ProductDTOs.GetProductResponse;
import com.backend.backendtienda.dto.ProductDTOs.UpdateProductRequest;
import com.backend.backendtienda.entity.Category;
import com.backend.backendtienda.entity.Product;
import com.backend.backendtienda.repository.CategoryRepository;
import com.backend.backendtienda.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final String folder;
    private final Path uploadsFolder;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          @Value("${app.images.products-folder}") String folder) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.folder = folder;
        this.uploadsFolder = Paths.get(folder).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadsFolder);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de imágenes", e);
        }
    }

    @Transactional
    public Integer create(CreateProductRequest req) {
        MultipartFile image = req.image();
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen es obligatoria");
        }

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe"));

        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String suffix = (extension != null && extension.matches("[A-Za-z0-9]{1,10}")) ? "." + extension : "";
        String fileName = UUID.randomUUID() + suffix;

        try (InputStream in = image.getInputStream()) {
            Files.copy(in, uploadsFolder.resolve(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }

        Product product = new Product();
        product.setCategory(category);
        product.setName(req.name());
        product.setPrice(req.price());
        product.setStockQuantity(req.stockQuantity());
        product.setImage(fileName);

        return productRepository.save(product).getProductId();
    }

    @Transactional(readOnly = true)
    public List<GetProductResponse> getAll(String serverUrl, String categoryId) {
        List<Product> products = (categoryId == null || categoryId.equals("0"))
                ? productRepository.findAllByOrderByCreatedAtDesc()
                : productRepository.findByCategory_CategoryIdOrderByCreatedAtDesc(Integer.parseInt(categoryId));

        return products.stream()
                .map(p -> new GetProductResponse(
                        p.getProductId(),
                        String.valueOf(p.getCategory().getCategoryId()),
                        p.getCategory().getName(),
                        p.getName(),
                        p.getPrice(),
                        p.getStockQuantity(),
                        buildImageUrl(serverUrl, p.getImage())))
                .toList();
    }

    private String buildImageUrl(String serverUrl, String image) {
        if (image == null || image.isBlank()) {
            return null;
        }
        return serverUrl + "/" + folder + "/" + image;
    }



    //
    @Transactional
    public void update(UpdateProductRequest req) {
    Product product = productRepository.findById(req.productId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe"));

    Category category = categoryRepository.findById(req.categoryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe"));

    product.setCategory(category);
    product.setName(req.name());
    product.setPrice(req.price());
    product.setStockQuantity(req.stockQuantity());

    MultipartFile image = req.image();
    if (image != null && !image.isEmpty()) {
        String oldImage = product.getImage();

        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String suffix = (extension != null && extension.matches("[A-Za-z0-9]{1,10}")) ? "." + extension : "";
        String fileName = UUID.randomUUID() + suffix;

        try (InputStream in = image.getInputStream()) {
            Files.copy(in, uploadsFolder.resolve(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }

        product.setImage(fileName);

        
        if (oldImage != null && !oldImage.isBlank()) {
            try {
                Files.deleteIfExists(uploadsFolder.resolve(oldImage));
            } catch (IOException e) {
                
            }
        }
    }
    
}

@Transactional
public void delete(Integer id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe"));

    productRepository.delete(product);
}
}