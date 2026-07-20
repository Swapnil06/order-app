package com.practice.orderflow.dto;

import java.util.List;

public record CreateOrderRequest(String customerId, List<OrderItemRequest> items, String idempotencyKey) {
}
