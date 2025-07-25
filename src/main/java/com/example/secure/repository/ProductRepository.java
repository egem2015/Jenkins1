package com.example.secure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.secure.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    // CRUD methods will be automatically provided by MongoRepository
}