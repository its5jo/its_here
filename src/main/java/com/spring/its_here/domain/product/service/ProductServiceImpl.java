package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.infrastructure.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateCommand productCreateCommand) {

        Store store = storeRepository.findById(productCreateCommand.storeId()).orElseThrow();

        String imagePath = null;
        if (productCreateCommand.image() != null) {
            imagePath = imageStorage.store(productCreateCommand.image());
        }

        Product product = Product.create(
                productCreateCommand.name(),
                productCreateCommand.description(),
                productCreateCommand.hasHidden(),
                productCreateCommand.price(),
                imagePath,
                store
        );

        Product saved = productRepository.save(product);
        return new ProductCreateResponseDto(saved.getId());
    }

    @Override
    public void updateProduct(UUID productId) {

    }

    @Override
    public void deleteProduct(UUID productId) {

    }

    @Override
    public void getProduct(UUID productId) {

    }

    @Override
    public void getStoreProducts(UUID storeId) {

    }
}
