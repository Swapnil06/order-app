package com.practice.orderflow.enums;

/**
 * Order state machine. This is the backbone of the saga.
 *
 * Happy path:   CREATED -> INVENTORY_RESERVED -> PAYMENT_CONFIRMED -> COMPLETED
 * Failure paths:
 *   - Inventory unavailable:      CREATED -> INVENTORY_FAILED
 *   - Payment fails after reserve: INVENTORY_RESERVED -> PAYMENT_FAILED -> COMPENSATING -> CANCELLED
 *
 * Interview angle: this is exactly the kind of state machine interviewers ask you to
 * draw on a whiteboard. Know why COMPENSATING is a distinct state from CANCELLED
 * (it's the "in-flight rollback" state — if the app crashes mid-compensation, a
 * recovery job needs to know to resume the rollback, not assume it's done).
 */
public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED,
    COMPENSATING,
    COMPLETED,
    CANCELLED
}
