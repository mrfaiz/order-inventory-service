package com.example.order.domain;


import com.example.inventory.domain.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "order_lines")
public class OrderLine {
    @Id
    private UUID id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_lines_order")
    )
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_lines_product")
    )
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price_cents", nullable = false)
    private long unitPriceCents;

    protected OrderLine(){}

    public OrderLine(UUID id, Order order, Product product, int quantity, long unitPriceCents) {

        if(order == null){
            throw new IllegalArgumentException("Order can not be null");
        }

        if(product == null){
            throw  new IllegalArgumentException("Product can not be null");
        }

        if(quantity<=0){
            throw new IllegalArgumentException("Quantity must be >0");
        }
        if(unitPriceCents<0){
            throw new IllegalArgumentException("Unit price must be >=0");
        }

        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;

        // keep both sides in sync
        this.order.addLine(this);
    }

    public UUID getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getUnitPriceCents() {
        return unitPriceCents;
    }
}
