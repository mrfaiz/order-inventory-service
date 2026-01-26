package com.example.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name="uk_products_sku", columnNames = "sku")
})
public class Product {
    @Id
    private UUID id;

    @Column(nullable = false, length = 64)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long priceCents;

    @Column(nullable = false)
    private Instant createdAt;

    protected Product(){}

    public Product(UUID id, String sku, String name, long priceCents, Instant createdAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.priceCents = priceCents;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }


    public String getSku() {
        return sku;
    }

    public long getPriceCents() {
        return priceCents;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
