package com.example.order.api;

import java.time.Instant;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID id,
        String status,
        Instant createdAt,
        int lineCount
) {}