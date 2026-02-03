package com.example.inventory.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request to create or update products")
public record AddProductRequest(

        @NotEmpty
        @Valid
        @Schema(description = "Products to create or update", required = true)
        List<AddProductItemRequest> products
) {}
