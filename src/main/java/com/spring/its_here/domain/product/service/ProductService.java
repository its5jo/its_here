package com.spring.its_here.domain.product.service;

import java.util.UUID;

public interface ProductService {
    void createProduct();
    void updateProduct(UUID productId);
    void deleteProduct(UUID productId);
    void getProduct(UUID productId);
    void getStoreProducts(UUID storeId);
}
