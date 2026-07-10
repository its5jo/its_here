package com.spring.its_here.domain.product.controller;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.request.ProductCreateRequestDto;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.service.ProductService;
import com.spring.its_here.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(
            @RequestPart("product") ProductCreateRequestDto request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductCreateResponseDto productCreateResponseDto =
                productService.createProduct(ProductCreateCommand.of(request, image));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "상품 등록 성공",
                                productCreateResponseDto
                        )
                );
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Void> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stores/{storeId}/products")
    public ResponseEntity<Void> getStoreProducts(@PathVariable UUID storeId) {
        return ResponseEntity.ok().build();
    }
}
