package com.practice.orderflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync      // powers our CompletableFuture-based parallel calls (inventory + fraud check)
@EnableCaching     // powers Redis-backed product catalog cache
@EnableScheduling  // powers the outbox poller that publishes events reliably
public class OrderflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderflowApplication.class, args);
    }

}
