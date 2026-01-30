package com.example.order.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<OrderLine> lines = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt;

    protected Order(){}

    public Order(UUID id,  Instant createdAt) {
        if(id==null){
            throw new IllegalArgumentException("Order id must not be null");
        }
        this.id = id;
        this.status = OrderStatus.CREATED;
        this.createdAt = createdAt != null?createdAt: Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return List.copyOf(lines);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void addLine(OrderLine line){
        if(line==null){
            throw new IllegalArgumentException("OrderLine can not be null");
        }
        this.lines.add(line);
    }

    public void cancel(){
        if(this.status == OrderStatus.CANCELLED) return; //Idempotent
        if(this.status != OrderStatus.CREATED){
            throw new IllegalArgumentException("Only created orders can be cancel");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
