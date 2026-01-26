package com.example.order.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderLineRequest(@NotNull UUID productId, @Min(1) int quantity) {

}
