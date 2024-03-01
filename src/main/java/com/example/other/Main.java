package com.example.other;

public class Main {
    private static ProductRepository productRepository;
    public static void main(String[] args){

        Product product = productRepository.findProductById(1);
        Price price = new Price(-40); // throws error
        product.setPrice(price);
        productRepository.save(product);

    }
}
