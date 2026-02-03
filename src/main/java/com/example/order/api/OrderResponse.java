package com.example.order.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Schema(description = "Detailed representation of an order")
public record OrderResponse(
        @Schema(
                description = "Unique identifier of the order",
                example = "a3c8c8f5-4d0e-4a7a-b5c1-3c9e0f0b9c2d"
        )
        UUID id,

        @Schema(
                description = "Current status of the order",
                example = "CREATED"
        )
        String status,

        @Schema(
                description = "Timestamp when the order was created",
                example = "2026-01-22T15:30:00Z"
        )
        Instant createdAt,

        @Schema(
                description = "Order line items"
        )
        List<OrderLineResponse> lines
) {}