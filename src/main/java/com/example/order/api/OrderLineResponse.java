package com.example.order.api;

import java.util.UUID;

public record OrderLineResponse(
        UUID productId,
        String sku,
        String name,
        int quantity,
        long unitPriceCents
){}
