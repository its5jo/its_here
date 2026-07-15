package com.spring.its_here.domain.aihistory.dto.response;

import com.spring.its_here.domain.aihistory.entity.AiHistory;

import java.time.Instant;
import java.util.UUID;

public record AiHistoryResponseDto(
        UUID id,
        UUID productId,
        String prompt,
        String response,
        Instant createdAt,
        Long createdBy
) {
    public static AiHistoryResponseDto from(AiHistory aiHistory) {
        return new AiHistoryResponseDto(
                aiHistory.getId(),
                aiHistory.getProduct().getId(),
                aiHistory.getPrompt(),
                aiHistory.getResponse(),
                aiHistory.getCreatedAt(),
                aiHistory.getCreatedBy()
        );
    }
}
