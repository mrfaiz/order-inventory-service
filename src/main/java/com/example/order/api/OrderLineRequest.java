package com.example.order.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Single order line request")
public record OrderLineRequest(
        @NotNull
        @Schema(
                description = "Product identifier",
                example = "11111111-1111-1111-1111-111111111111"
        )
        UUID productId,

        @Min(1)
        @Schema(
                description = "Quantity of the product to order (must be at least 1)",
                example = "2"
        )
        int quantity) {}
