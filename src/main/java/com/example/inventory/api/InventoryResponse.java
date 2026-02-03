package com.example.inventory.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
@Schema(description = "Inventory for a product")
public record InventoryResponse(
        @Schema(description = "Product identifier", example = "11111111-1111-1111-1111-111111111111")
        UUID productId,

        @Schema(description = "Available quantity", example = "10")
        int quantity
) {
}
