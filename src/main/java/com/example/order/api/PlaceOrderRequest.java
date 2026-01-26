package com.example.order.api;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlaceOrderRequest(@NotEmpty List<OrderLineRequest> lines) {
}
