package com.optimistic.lock.demo_optimistic_lock.service;

import com.optimistic.lock.demo_optimistic_lock.entity.Product;
import com.optimistic.lock.demo_optimistic_lock.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void updateStock(Long productId, Integer newStock) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found!"));
        try {
            Thread.sleep(100);
        }catch (InterruptedException e) {
            System.out.println(e);
        }
        product.setStock(newStock);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
          .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
