package com.example.order.api;

import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Orders", description = "Order placement, retrieval, and cancellation")
@RestController
@RequestMapping("/orders")
public class OrderController {
    private  final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService service, OrderRepository orderRepository){
        this.orderService = service;
        this.orderRepository = orderRepository;
    }

    @Operation(
            summary = "Place an order",
            description = "Creates an order and deducts inventory atomically. Supports retries via Idempotency-Key."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created (or existing order returned for same idempotency key)"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product/Inventory not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Insufficient stock / conflict", content = @Content)
    })


    @PostMapping
    public ResponseEntity<UUID> placeOrder(
            @Parameter(description = "Optional idempotency key to make POST /orders retry-safe", example = "demo-order-1")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceOrderRequest.class))
            )
            @RequestBody @Valid PlaceOrderRequest request
    ) {
        UUID orderId = orderService.placeOrder(request, idempotencyKey);
        return ResponseEntity.ok(orderId);
    }


    @Operation(summary = "Get an order by id", description = "Returns an order with its order lines.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{orderId}")
    public OrderResponse get(@Parameter(description = "Order id", required = true) @PathVariable UUID orderId){
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

    @Operation(summary = "Cancel an order", description = "Cancels an order and restores inventory. Calling this endpoint multiple times is safe (idempotent).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled (or already cancelled)"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled in current state", content = @Content)
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List order summaries", description = "Returns lightweight order list with line counts (efficient for large datasets).")
    @ApiResponse(responseCode = "200", description = "List of order summaries")
    @GetMapping("/summary")
    public List<OrderSummaryResponse> listAll(){
        return orderRepository.findOrderSummariesRaw().stream()
                .map(r -> new OrderSummaryResponse(
                        (UUID) r[0],
                        r[1].toString(),
                        (java.time.Instant) r[2],
                        ((Long) r[3]).intValue()
                ))
                .toList();

    }
}
