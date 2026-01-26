package com.example.inventory.api;

import java.util.List;

public record AddProductRequest(List<ProductResponse> products) {
}
