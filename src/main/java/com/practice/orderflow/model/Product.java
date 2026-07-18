package com.practice.orderflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @Version is the whole ballgame here for concurrency safety.
 *
 * Interview angle: "How do you prevent overselling when two customers buy the
 * last item at the same time?" -> Optimistic locking. JPA adds a WHERE version = ?
 * clause to the UPDATE. If two transactions read version=5 concurrently and both
 * try to decrement stock, only the first COMMIT succeeds; the second throws
 * OptimisticLockException because its WHERE clause no longer matches (version
 * is now 6). We catch that and retry or fail fast — never silently overwrite.
 *
 * Contrast with pessimistic locking (SELECT ... FOR UPDATE): that blocks other
 * transactions at read time instead of failing at commit time. Optimistic is
 * better here because stock checks are read-heavy and contention is rare relative
 * to reads — you don't want every product read to take a row lock.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stockQuantity;

    @Version
    private Long version;
}
