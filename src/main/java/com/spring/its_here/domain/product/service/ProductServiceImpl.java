package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.aihistory.repository.AiHistoryRepository;
import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.command.ProductUpdateCommand;
import com.spring.its_here.domain.product.dto.request.ProductSearchCondition;
import com.spring.its_here.domain.product.dto.response.*;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.infrastructure.ai.AiClient;
import com.spring.its_here.infrastructure.ai.ProductDescriptionPromptGenerator;
import com.spring.its_here.infrastructure.ai.Prompt;
import com.spring.its_here.infrastructure.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final AiClient aiClient;
    private final ProductDescriptionPromptGenerator productDescriptionPromptGenerator;
    private final AiHistoryRepository aiHistoryRepository;
    private final ProductWriter productWriter;

    @Override
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

        String description = productCreateCommand.description();
        String aiResponse = null;
        Prompt prompt = null;

        if (productCreateCommand.useAiDescription()) {
            log.debug("상품 설명 AI 생성 요청. storeId={}, userId={}", store.getId(), loginUserId);
            prompt = productDescriptionPromptGenerator.generate(
                    productCreateCommand.name(),
                    productCreateCommand.price()
            );
            aiResponse = aiClient.generateDescription(prompt);
            description = aiResponse;
        }

        String imagePath = null;
        if (productCreateCommand.image() != null) {
            imagePath = imageStorage.store(productCreateCommand.image());
            log.debug("상품 이미지 생성 성공. path={}", imagePath);
        }

        return productWriter.save(
                productCreateCommand,
                loginUserId,
                description,
                imagePath,
                prompt,
                aiResponse
        );

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
    @Transactional(readOnly = true)
    public ProductCursorResponseDto searchStoreProducts(
            ProductSearchCondition condition,
            UUID storeId
    ) {
        log.debug(
                "가게 상품 목록 조회 요청. storeId={}, cursor={}, idAfter={}, sortBy={}, sortDirection={}, limit={}",
                storeId,
                condition.cursor(),
                condition.idAfter(),
                condition.sortBy(),
                condition.sortDirection(),
                condition.limit()
        );

        storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.warn("상품 목록 조회 실패 - 가게를 찾을 수 없음. storeId={}", storeId);
                    return new ItsHereException(ErrorCode.STORE_NOT_FOUND);
                });

        Pageable pageable = createPageable(condition);

        Instant cursor = condition.cursor() == null
                ? null
                : Instant.parse(condition.cursor());

        boolean isFirstPage = cursor == null;

        Slice<Product> productSlice = productRepository.searchProductsByCursor(
                storeId,
                cursor,
                condition.idAfter(),
                condition.sortDirection().name(),
                isFirstPage,
                pageable
        );

        List<Product> products = productSlice.getContent();

        List<ProductResponseDto> content = products.stream()
                .map(ProductResponseDto::from)
                .toList();

        String nextCursor = null;
        UUID nextId = null;

        if (productSlice.hasNext()) {
            Product lastProduct = products.get(products.size() - 1);
            nextCursor = lastProduct.getCreatedAt().toString();
            nextId = lastProduct.getId();
        }

        log.info(
                "가게 상품 목록 조회 완료. storeId={}, resultCount={}, hasNext={}, nextCursor={}, nextId={}",
                storeId,
                content.size(),
                productSlice.hasNext(),
                nextCursor,
                nextId
        );

        return new ProductCursorResponseDto(
                content,
                new ProductCursorPageInfo(
                        "CURSOR",
                        nextCursor,
                        nextId,
                        productSlice.hasNext(),
                        condition.sortBy().getValue(),
                        condition.sortDirection()
                )
        );
    }

    private Pageable createPageable(ProductSearchCondition condition) {
        Sort.Direction direction =
                condition.sortDirection().toSpringDirection();

        Sort sort = Sort.by(direction, condition.sortBy().getValue())
                .and(Sort.by(direction, "id"));

        return PageRequest.of(0, condition.limit(), sort);

    }
}
