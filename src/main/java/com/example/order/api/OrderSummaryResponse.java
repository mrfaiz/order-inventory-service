package com.example.order.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Lightweight order summary")
public record OrderSummaryResponse(

        @Schema(
                description = "Order identifier",
                example = "a3c8c8f5-4d0e-4a7a-b5c1-3c9e0f0b9c2d"
        )
        UUID id,

        @Schema(
                description = "Order status",
                example = "CREATED"
        )
        String status,

        @Schema(
                description = "Order creation timestamp",
                example = "2026-01-22T15:30:00Z"
        )
        Instant createdAt,

        @Schema(
                description = "Number of order lines",
                example = "3"
        )
        int lineCount
) {}
