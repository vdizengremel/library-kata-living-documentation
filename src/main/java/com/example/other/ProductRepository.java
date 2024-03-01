package com.example.other;

public interface ProductRepository {
    Product findProductById(int id);
    void save(Product product);
}
