package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.command.ProductUpdateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductUpdateResponseDto;
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

        Store store = storeRepository.findById(productCreateCommand.storeId()).orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));

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
    @Transactional
    public ProductUpdateResponseDto updateProduct(ProductUpdateCommand productUpdateCommand, Long loginUserId) {

        UserEntity user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.OWNER) {
            log.warn("상품 수정 권한 없음. userId={}, role={}", loginUserId, user.getRole());
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        Product product = productRepository.findById(productUpdateCommand.productId())
                .orElseThrow(() -> new ItsHereException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!loginUserId.equals(product.getStore().getUser().getId())) {
            log.warn("상품 수정 권한 없음. userId={}, productId={}, storeId={}",
                    loginUserId,
                    product.getId(),
                    product.getStore().getId())
            ;
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        product.update(
                productUpdateCommand.name(),
                productUpdateCommand.description(),
                productUpdateCommand.hasHidden(),
                productUpdateCommand.price(),
                productUpdateCommand.imageUrl()
        );

        log.info(
                "상품 수정 처리 요청 완료. productId={}, storeId={}, userId={}",
                product.getId(),
                product.getStore().getId(),
                loginUserId
        );
        return new ProductUpdateResponseDto(product.getId());
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId, Long loginUserId) {
        UserEntity user = userRepository.findById(loginUserId).orElseThrow(() -> new ItsHereException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.OWNER) {
            log.warn("상품 삭제 권한 없음. userId={}, role={}", loginUserId, user.getRole());
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new ItsHereException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!loginUserId.equals(product.getStore().getUser().getId())) {
            log.warn("상품 삭제 권한 없음. userId={}, productId={}, storeId={}",
                    loginUserId,
                    product.getId(),
                    product.getStore().getId()
            );
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        product.delete(loginUserId);

        log.info("상품 삭제 처리 요청 완료. productId={}, storeId={}, userId={}",
                product.getId(),
                product.getStore().getId(),
                loginUserId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ItsHereException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductResponseDto(
                product.getName(),
                product.getDescription(),
                product.isHasHidden(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

    @Override
    public void getStoreProducts(UUID storeId) {

    }
}
