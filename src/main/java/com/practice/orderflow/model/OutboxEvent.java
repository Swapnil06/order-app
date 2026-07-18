package com.practice.orderflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * The Outbox Pattern, explained for interviews:
 *
 * Problem: when a service updates its DB AND publishes an event (e.g., "OrderCreated"),
 * doing both isn't atomic. If you save to DB, then the app crashes before publishing
 * to Kafka, you have a phantom order the rest of the system never hears about. If you
 * publish first and the DB write fails, you have an event for an order that doesn't exist.
 *
 * Solution: write the event to an "outbox" TABLE in the SAME local transaction as the
 * business data (order, inventory, etc). Since it's the same DB transaction, it's atomic
 * by definition — either both commit or neither does. A separate poller (see
 * OutboxPublisherJob) then reads unpublished rows and pushes them to the message broker
 * (Kafka in production; here, an in-memory event bus for local runnability), marking
 * them published once acknowledged.
 *
 * This is the standard answer to "how do you guarantee at-least-once delivery of
 * domain events without distributed transactions / 2PC."
 */
@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String aggregateType; // e.g. "Order"

    @Column(nullable = false)
    private UUID aggregateId;     // e.g. the order's id

    @Column(nullable = false)
    private String eventType;     // e.g. "OrderCreated", "PaymentFailed"

    @Lob
    @Column(nullable = false)
    private String payload;       // JSON snapshot of the event

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;  // null until the poller successfully publishes it
}
