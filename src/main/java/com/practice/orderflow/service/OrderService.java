package com.practice.orderflow.service;

import com.practice.orderflow.dto.CreateOrderRequest;
import com.practice.orderflow.dto.OrderItemRequest;
import com.practice.orderflow.enums.OrderStatus;
import com.practice.orderflow.model.Order;
import com.practice.orderflow.model.OrderItem;
import com.practice.orderflow.model.Product;
import com.practice.orderflow.repository.OrderRepository;
import com.practice.orderflow.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, InventoryService inventoryService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.productRepository = productRepository;
    }

    public Order placeOrder(CreateOrderRequest request) {
        Optional<Order> existingOrder = orderRepository.findByIdempotencyKey(request.idempotencyKey());
        if(existingOrder.isPresent()) {
            log.info("Order already exists for idempotency key: {}", request.idempotencyKey());
            return existingOrder.get();
        }

        Order order = createOrder(request);
        //TODO: reserve inventory for each item
        return  order;
    }

    private Order createOrder(CreateOrderRequest request) {
        // build Order, build OrderItems, calculate totalAmount, save, return
        Order newOrder = new Order();
        newOrder.setCustomerId(request.customerId());
        newOrder.setIdempotencyKey(request.idempotencyKey());
        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setCreatedAt(Instant.now());

        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemRequest orderItemRequest : request.items()) {
            Product product = productRepository.findById(orderItemRequest.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(orderItemRequest.quantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItems.add(orderItem);
        }

        BigDecimal totalAmount = orderItems.stream().
                map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        newOrder.setItems(orderItems);
        newOrder.setTotalAmount(totalAmount);

        return orderRepository.save(newOrder);

    }

}
