package com.example.order.service;

import com.example.inventory.domain.InventoryItem;
import com.example.inventory.domain.Product;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.order.api.PlaceOrderRequest;
import com.example.order.domain.Order;
import com.example.order.domain.OrderLine;
import com.example.order.domain.OrderStatus;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
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
    public UUID placeOrder(PlaceOrderRequest request, String idempotencyKey) {

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get().getId();
            }
        }

        Order order = new Order(
                UUID.randomUUID(),
                Instant.now(),
                idempotencyKey
        );
        System.out.println(order.getIdempotencyKey());
        for (var lineReq : request.lines()) {

            InventoryItem inventory = inventoryItemRepository
                    .findByProductIdForUpdate(lineReq.productId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Inventory not found for productId=" + lineReq.productId()
                            )
                    );

            inventory.deduct(lineReq.quantity());

            Product product = inventory.getProduct();

           new OrderLine(
                    UUID.randomUUID(),
                    order,
                    product,
                    lineReq.quantity(),
                    product.getPriceCents()
            );

            // OrderLine constructor already does:
            // order.addLine(this)
        }

        try {
            orderRepository.save(order);
            return order.getId();
        } catch (DataIntegrityViolationException ex) {
            // 5️⃣ Race condition handling for same idempotency key
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                return orderRepository.findByIdempotencyKey(idempotencyKey)
                        .map(Order::getId)
                        .orElseThrow(() -> ex);
            }
            throw ex;
        }
    }


    @Transactional
    public  void cancelOrder(UUID orderId){
        Order order = orderRepository.findByIdWithLines(orderId)
                .orElseThrow(()-> new IllegalArgumentException("Order not found"));

        if(order.getStatus() == OrderStatus.CANCELLED) return;

        for(var line: order.getLines()){
            UUID productId = line.getProduct().getId();

            InventoryItem inventoryItem = inventoryItemRepository.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found"));
            inventoryItem.add(line.getQuantity());
        }
        order.cancel();
        orderRepository.save(order);

    }
}
