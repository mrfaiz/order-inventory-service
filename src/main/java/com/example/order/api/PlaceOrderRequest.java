package com.example.order.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Request to place an order")
public record PlaceOrderRequest(
        @NotEmpty
        @Valid
        @Schema(
                description = "Order lines to be placed",
                required = true
        )
        List<OrderLineRequest> lines
) {
}