package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;

import java.util.UUID;

public interface ProductService {
    ProductCreateResponseDto createProduct(ProductCreateCommand productCreateCommand, Long loginUserId);

    void updateProduct(UUID productId);

    void deleteProduct(UUID productId);

    void getProduct(UUID productId);

    void getStoreProducts(UUID storeId);
}
