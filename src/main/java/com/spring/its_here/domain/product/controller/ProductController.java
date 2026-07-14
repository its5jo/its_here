package com.spring.its_here.domain.product.controller;

import com.spring.its_here.domain.product.controller.docs.ProductApi;
import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.command.ProductUpdateCommand;
import com.spring.its_here.domain.product.dto.request.ProductCreateRequestDto;
import com.spring.its_here.domain.product.dto.request.ProductSearchCondition;
import com.spring.its_here.domain.product.dto.request.ProductUpdateRequestDto;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductCursorResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductUpdateResponseDto;
import com.spring.its_here.domain.product.service.ProductService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
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
            @Valid @RequestPart("product") ProductCreateRequestDto request,
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
    public ResponseEntity<ApiResponse<ProductUpdateResponseDto>> updateProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequestDto request
    ) {
        ProductUpdateResponseDto productUpdateResponseDto = productService.updateProduct(
                ProductUpdateCommand.of(request, productId),
                userDetails.getUserId()
        );
        return ResponseEntity.ok().body(
                ApiResponse.success(
                        "상품 수정 성공",
                        productUpdateResponseDto
                )
        );
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
                        "상품 단일 조회 성공", productResponseDto
                )
        );
    }

    @GetMapping("/stores/{storeId}/products")
    public ResponseEntity<ApiResponse<ProductCursorResponseDto>> getStoreProducts(
            @Valid ProductSearchCondition condition,
            @PathVariable UUID storeId
    ) {
        ProductCursorResponseDto productCursorResponseDto = productService.searchStoreProducts(condition, storeId);
        return ResponseEntity.ok().body(ApiResponse.success("가게 상품 목록 조회 성공", productCursorResponseDto));
    }
}
