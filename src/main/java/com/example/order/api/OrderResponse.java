package com.example.order.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String status,
        Instant createdAt,
        List<OrderLineResponse> lines
) {}