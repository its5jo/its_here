package com.spring.its_here.domain.product.controller;

import com.spring.its_here.domain.product.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductController {

    @PostMapping("/products")
    public ResponseEntity<Void> createProduct() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
