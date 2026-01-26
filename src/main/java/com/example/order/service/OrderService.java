package com.example.order.service;

import com.example.inventory.domain.InventoryItem;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.order.api.PlaceOrderRequest;
import com.example.order.domain.Order;
import com.example.order.domain.OrderLine;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {
    private final InventoryItemRepository inventoryItemRepository;
    private final OrderRepository orderRepository;

    public OrderService(InventoryItemRepository inventoryItemRepository, OrderRepository orderRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public UUID placeOrder(@NonNull PlaceOrderRequest request){
        Order order =  new Order(UUID.randomUUID(), Instant.now());
        for(var lineReq: request.lines()){
            InventoryItem inventoryItem = inventoryItemRepository
                    .findByProductIdForUpdate(lineReq.productId())
                    .orElseThrow(()-> new IllegalArgumentException("Product not found"));

            inventoryItem.deduct(lineReq.quantity());

            var product = inventoryItem.getProduct();
            OrderLine line = new OrderLine(
                    UUID.randomUUID(),
                    order,
                    product,
                    lineReq.quantity(),
                    product.getPriceCents()
            );

        }
        orderRepository.save(order);
        return order.getId();
    }
}
