package com.example.order.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Order line details")
public record OrderLineResponse(

        @Schema(
                description = "Product identifier",
                example = "11111111-1111-1111-1111-111111111111"
        )
        UUID productId,

        @Schema(
                description = "Product SKU",
                example = "SKU-001"
        )
        String sku,

        @Schema(
                description = "Product name",
                example = "Wireless Mouse"
        )
        String name,

        @Schema(
                description = "Quantity ordered",
                example = "2"
        )
        int quantity,

        @Schema(
                description = "Unit price in cents",
                example = "1999"
        )
        long unitPriceCents
) {}
