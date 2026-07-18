package com.practice.orderflow.enums;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    TIMED_OUT   // distinct from FAILED — timeout means we don't know the real outcome,
                // which is precisely why idempotency keys matter on retry.
}
