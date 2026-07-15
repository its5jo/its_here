package com.spring.its_here.domain.aihistory.service;

import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryCursorPageInfo;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryCursorResponseDto;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryResponseDto;
import com.spring.its_here.domain.aihistory.entity.AiHistory;
import com.spring.its_here.domain.aihistory.repository.AiHistoryRepository;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiHistoryServiceImpl implements AiHistoryService {

    private final AiHistoryRepository aiHistoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public AiHistoryResponseDto getAiHistory(UUID aiHistoryId) {
        AiHistory aiHistory = aiHistoryRepository.findById(aiHistoryId).orElseThrow(() -> new ItsHereException(ErrorCode.AI_HISTORY_NOT_FOUND));
        return AiHistoryResponseDto.from(aiHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiHistoryResponseDto> getAiHistories() {
        List<AiHistory> all = aiHistoryRepository.findAllWithProduct();
        return all.stream().map(AiHistoryResponseDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AiHistoryCursorResponseDto searchAiHistories(
            AiHistorySearchCondition condition,
            UUID productId
    ) {
        log.debug(
                "AI 이력 목록 조회 요청. productId={}, cursor={}, idAfter={}, sortBy={}, sortDirection={}, limit={}",
                productId,
                condition.cursor(),
                condition.idAfter(),
                condition.sortBy(),
                condition.sortDirection(),
                condition.limit()
        );

        productRepository.findById(productId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.PRODUCT_NOT_FOUND));

        Pageable pageable = createPageable(condition);

        Slice<AiHistory> aiHistorySlice =
                aiHistoryRepository.searchAiHistoriesByCursor(
                        productId,
                        condition.cursor(),
                        condition.idAfter(),
                        condition.sortDirection().name(),
                        pageable
                );

        List<AiHistory> histories = aiHistorySlice.getContent();

        List<AiHistoryResponseDto> content = histories.stream()
                .map(AiHistoryResponseDto::from)
                .toList();

        String nextCursor = null;
        UUID nextId = null;

        if (aiHistorySlice.hasNext()) {
            AiHistory lastHistory = histories.get(histories.size() - 1);
            nextCursor = lastHistory.getCreatedAt().toString();
            nextId = lastHistory.getId();
        }

        return new AiHistoryCursorResponseDto(
                content,
                new AiHistoryCursorPageInfo(
                        "CURSOR",
                        nextCursor,
                        nextId,
                        aiHistorySlice.hasNext(),
                        condition.sortBy().getValue(),
                        condition.sortDirection()
                )
        );
    }

    private Pageable createPageable(AiHistorySearchCondition condition) {
        Sort.Direction direction =
                condition.sortDirection().toSpringDirection();

        Sort sort = Sort.by(direction, condition.sortBy().getValue())
                .and(Sort.by(direction, "id"));

        return PageRequest.of(0, condition.limit(), sort);
    }

}
