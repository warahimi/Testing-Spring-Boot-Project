package com.cwc.Unit_Integration_Testing.repository;

import com.cwc.Unit_Integration_Testing.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
}
