package com.example.inventory.api;

import com.example.inventory.domain.Product;
import com.example.inventory.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Tag(name = "Products", description = "Product management APIs")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository pr) {
        this.productRepository = pr;
    }

    @Operation(summary = "List products", description = "Returns all products.")
    @ApiResponse(responseCode = "200", description = "List of products")
    @GetMapping
    public List<ProductResponse> list() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getPriceCents()))
                .toList();
    }

    @Operation(summary = "Get product by id", description = "Returns a product by its id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ProductResponse getById(
            @Parameter(description = "Product id", required = true)
            @PathVariable UUID id
    ) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));

        return new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getPriceCents());
    }

    @Operation(summary = "Create products", description = "Creates one or more products.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Products created"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid AddProductRequest request) {
        Instant now = Instant.now();
        for (var p : request.products()) {
            productRepository.save(new Product(
                    p.productId(), p.sku(), p.name(), p.unitPriceCents(), now
            ));
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update products", description = "Upserts one or more products by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid AddProductRequest request) {
        Instant now = Instant.now();
        for (var p : request.products()) {
            productRepository.save(new Product(
                    p.productId(), p.sku(), p.name(), p.unitPriceCents(), now
            ));
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete product", description = "Deletes a product by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Product id", required = true)
            @PathVariable UUID id
    ) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
