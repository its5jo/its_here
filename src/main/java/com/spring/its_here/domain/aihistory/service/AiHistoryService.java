package com.spring.its_here.domain.aihistory.service;

import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;

import java.util.UUID;

public interface AiHistoryService {
    void getAiHistory();

    void getAiHistories();

    void searchAiHistories(AiHistorySearchCondition condition, UUID productId);

}
