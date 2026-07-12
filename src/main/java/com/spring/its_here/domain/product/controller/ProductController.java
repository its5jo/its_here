package com.spring.its_here.domain.product.controller;

import com.spring.its_here.domain.product.controller.docs.ProductApi;
import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.request.ProductCreateRequestDto;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.service.ProductService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductService productService;

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<ApiResponse<ProductCreateResponseDto>> createProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("product") ProductCreateRequestDto request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductCreateResponseDto productCreateResponseDto =
                productService.createProduct(ProductCreateCommand.of(request, image), userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
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
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID productId
    ) {
        productService.deleteProduct(productId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/{productId}")
    @Override
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(@PathVariable UUID productId) {
        ProductResponseDto productResponseDto = productService.getProduct(productId);
        return ResponseEntity.ok().body(ApiResponse.success(
                        "SUCCESS", productResponseDto
                )
        );
    }

    @GetMapping("/stores/{storeId}/products")
    public ResponseEntity<Void> getStoreProducts(@PathVariable UUID storeId) {
        return ResponseEntity.ok().build();
    }
}
