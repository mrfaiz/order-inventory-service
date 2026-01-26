package com.example.inventory.api;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String sku,
        String name,
        long unitPriceCents
        ) {
}
