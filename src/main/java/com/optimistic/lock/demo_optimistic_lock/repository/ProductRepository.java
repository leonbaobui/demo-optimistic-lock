package com.optimistic.lock.demo_optimistic_lock.repository;

import com.optimistic.lock.demo_optimistic_lock.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
