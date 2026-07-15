package com.spring.its_here.domain.aihistory.service;

import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryCursorResponseDto;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryResponseDto;

import java.util.List;
import java.util.UUID;

public interface AiHistoryService {
    AiHistoryResponseDto getAiHistory(UUID aiHistoryId);

    List<AiHistoryResponseDto> getAiHistories();

    AiHistoryCursorResponseDto searchAiHistories(AiHistorySearchCondition condition, UUID productId);

}
