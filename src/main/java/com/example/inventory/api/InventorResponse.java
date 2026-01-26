package com.example.inventory.api;

import java.util.UUID;

public record InventorResponse(
        UUID productId,
        int quantity
) {
}
