package com.example.inventory.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Single product to create or update")
public record AddProductItemRequest(

        @NotNull
        @Schema(
                description = "Product identifier",
                example = "11111111-1111-1111-1111-111111111111",
                required = true
        )
        UUID productId,

        @NotBlank
        @Schema(
                description = "Stock keeping unit",
                example = "SKU-001",
                required = true
        )
        String sku,

        @NotBlank
        @Schema(
                description = "Product name",
                example = "Wireless Mouse",
                required = true
        )
        String name,

        @Min(0)
        @Schema(
                description = "Unit price in cents",
                example = "1999",
                required = true
        )
        long unitPriceCents
) {}
