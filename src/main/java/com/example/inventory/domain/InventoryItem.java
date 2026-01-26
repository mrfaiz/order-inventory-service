package com.example.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "inventory_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product_id",
                        columnNames = "product_id"
                )
        }
)
public class InventoryItem {
    @Id
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inventory_product")
    )
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Instant updatedAt;

    protected InventoryItem(){}

    public InventoryItem(UUID id, Product product, int quantity, Instant updatedAt) {
        if(product==null){
            throw new IllegalArgumentException("Product must not be null");
        }
        if(quantity<0){
            throw new IllegalArgumentException("Quantity must be >0");
        }
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public  void add(int amount){
        if(amount<=0){
            throw new IllegalArgumentException("Amount has to be >0");
        }
        this.quantity += amount;
        this.updatedAt = Instant.now();
    }

    public void deduct(int amount){
        if(amount<=0){
            throw new IllegalArgumentException("Amount has to be >0");
        }
        if(this.quantity<amount){
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.quantity -= amount;
        this.updatedAt = Instant.now();
    }
}
