package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.infrastructure.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateCommand productCreateCommand, Long loginUserId) {
        UserEntity user = userRepository.findById(loginUserId).orElseThrow(() -> new ItsHereException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.OWNER) {
            log.warn("상품 등록 권한 없음. userId={}, role={}", loginUserId, user.getRole());
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        Store store = storeRepository.findById(productCreateCommand.storeId()).orElseThrow(() -> new ItsHereException(ErrorCode.NOT_FOUND));

        if (!loginUserId.equals(store.getUser().getId())) {
            log.warn("해당 가게의 소유자와 다름. userId={}, storeId={}", loginUserId, store.getId());
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        String imagePath = null;
        if (productCreateCommand.image() != null) {
            imagePath = imageStorage.store(productCreateCommand.image());
            log.debug("상품 이미지 생성 성공. path={}", imagePath);
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
        log.info("상품 생성 완료. productId={}, storeId={}, userId={}", saved.getId(), store.getId(), loginUserId);
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
