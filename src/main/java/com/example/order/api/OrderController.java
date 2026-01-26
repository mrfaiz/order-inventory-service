package com.example.order.api;

import com.example.order.domain.Order;
import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private  final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService service, OrderRepository orderRepository){
        this.orderService = service;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<UUID> placeOrder(@RequestBody @Valid PlaceOrderRequest request)
    {
        UUID orderId = orderService.placeOrder(request);
        return  ResponseEntity.ok(orderId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable UUID orderId){
        System.out.println(orderId);
        var d = orderRepository.findAll();
        System.out.println(d.size());
        System.out.println(d.getFirst().getLines().size());
        var order = orderRepository.findByIdWithLines(orderId)
                .orElseThrow(() -> new IllegalArgumentException(("Order not found")));
        var lines = order.getLines().stream()
                .map(l -> new OrderLineResponse(
                        l.getProduct().getId(),
                        l.getProduct().getSku(),
                        l.getProduct().getName(),
                        l.getQuantity(),
                        l.getUnitPriceCents()
                ))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getCreatedAt(),
                lines
        );
    }
}
