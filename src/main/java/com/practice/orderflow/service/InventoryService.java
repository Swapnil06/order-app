package com.practice.orderflow.service;

import com.practice.orderflow.model.Product;
import com.practice.orderflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class InventoryService {
    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void reserveStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        if(product.getStockQuantity() >= quantity) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        } else {
            throw new RuntimeException("Insufficient stock for product: " + productId + ". Requested: "
                + quantity + ", Available: " + product.getStockQuantity());
        }
    }

    public void releaseStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }
}
