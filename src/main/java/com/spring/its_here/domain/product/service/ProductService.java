package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.command.ProductUpdateCommand;
import com.spring.its_here.domain.product.dto.request.ProductSearchCondition;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductCursorResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductUpdateResponseDto;

import java.util.UUID;

public interface ProductService {
    ProductCreateResponseDto createProduct(ProductCreateCommand productCreateCommand, Long loginUserId);

    ProductUpdateResponseDto updateProduct(ProductUpdateCommand productUpdateCommand, Long loginUserId);

    void deleteProduct(UUID productId, Long loginUserId);

    ProductResponseDto getProduct(UUID productId);

    ProductCursorResponseDto searchStoreProducts(ProductSearchCondition condition, UUID storeId);
}
