package com.example.inventory.api;

import com.example.inventory.domain.Product;
import com.example.inventory.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository pr){
        this.productRepository = pr;
    }

    @GetMapping
    public List<ProductResponse> list(){
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getSku(),p.getName(),p.getPriceCents()))
                .toList();
    }

    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable UUID id){
        return productRepository.findById(id);
    }


    @PostMapping("/add")
    public void add(@RequestBody @Valid AddProductRequest request){
        for(var p : request.products()){
            productRepository.save(new Product(p.productId(),p.sku(),p.name(), p.unitPriceCents(), Instant.now()));
        }
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid AddProductRequest request){
        Instant now = Instant.now();
        for(var p : request.products()){
            productRepository.save(new Product(p.productId(),p.sku(),p.name(), p.unitPriceCents(), now));
        }
        return  ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
     if(! productRepository.existsById(id)){
         return ResponseEntity.notFound().build();
     }
     productRepository.deleteById(id);
     return ResponseEntity.noContent().build();
    }
}
