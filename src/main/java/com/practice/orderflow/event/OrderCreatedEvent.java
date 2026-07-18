package com.practice.orderflow.event;

import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, String customerId) {

}
