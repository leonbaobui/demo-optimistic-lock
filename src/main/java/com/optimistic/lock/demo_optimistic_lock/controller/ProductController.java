package com.optimistic.lock.demo_optimistic_lock.controller;

import com.optimistic.lock.demo_optimistic_lock.entity.Product;
import com.optimistic.lock.demo_optimistic_lock.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}/simulate-optimistic-lock")
    public ResponseEntity<String> simulateOptimisticLock(@PathVariable Long id) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<?>> futures = new ArrayList<>();
        List<String> exceptions = Collections.synchronizedList(new ArrayList<>());
        int randomNum = (int) (Math.random() * 100);
        int randomNum2 = (int) (Math.random() * 100);
        futures.add(executorService.submit(() -> {
            try {
                productService.updateStock(id, randomNum);
            } catch (ObjectOptimisticLockingFailureException ex) {
                exceptions.add("Task1 failed: " + ex.getMessage());
            }
        }));

        futures.add(executorService.submit(() -> {
            try {
                productService.updateStock(id, randomNum2);
            } catch (ObjectOptimisticLockingFailureException ex) {
                exceptions.add("Task2 failed: " + ex.getMessage());
            }
        }));

        executorService.shutdown();

        try {
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred during execution: " + e.getMessage());
        }

        if (!exceptions.isEmpty()) {
            return ResponseEntity.status(409).body(String.join("\n", exceptions));
        }

        return ResponseEntity.ok("Simulation complete! No conflicts detected.");
    }
}
