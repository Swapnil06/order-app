package com.practice.orderflow.dto;

import java.util.UUID;

public record OrderItemRequest(UUID productId, int quantity) {
}
