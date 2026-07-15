package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.aihistory.entity.AiHistory;
import com.spring.its_here.domain.aihistory.repository.AiHistoryRepository;
import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.infrastructure.ai.Prompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductWriter {

    private final ProductRepository productRepository;
    private final AiHistoryRepository aiHistoryRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ProductCreateResponseDto save(
            ProductCreateCommand productCreateCommand,
            Long loginUserId,
            String description,
            String imagePath,
            Prompt prompt,
            String aiResponse
    ) {
        long startedAt = System.nanoTime();
        Store store = storeRepository.findById(productCreateCommand.storeId())
                .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));

        // NOTE: 소유자 바뀐상황 고려, 재검증
        if (!loginUserId.equals(store.getUser().getId())) {
            log.warn(
                    "상품 저장 시점 가게 소유권 불일치. userId={}, storeId={}",
                    loginUserId,
                    store.getId()
            );

            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);

        }

        Product product = Product.create(
                productCreateCommand.name(),
                description,
                productCreateCommand.hasHidden(),
                productCreateCommand.price(),
                imagePath,
                store
        );

        Product savedProduct = productRepository.save(product);
        log.info("상품 생성 완료. productId={}, storeId={}, userId={}", savedProduct.getId(), store.getId(), loginUserId);

        if (aiResponse != null) {
            AiHistory savedAiHistory = aiHistoryRepository.save(
                    AiHistory.create(
                            savedProduct,
                            prompt.serialize(),
                            aiResponse
                    )
            );
            log.info(
                    "상품 설명 AI 생성 및 이력 저장 완료. aiHistoryId={}, productId={}, storeId={}, userId={}",
                    savedAiHistory.getId(),
                    savedProduct.getId(),
                    store.getId(),
                    loginUserId
            );
        }
        long elapsedMs =
                (System.nanoTime() - startedAt) / 1_000_000;
        log.info(
                "상품 DB 저장 트랜잭션 처리 시간. elapsedMs={}, storeId={}, userId={}",
                elapsedMs,
                productCreateCommand.storeId(),
                loginUserId
        );
        return new ProductCreateResponseDto(savedProduct.getId());
    }

}
