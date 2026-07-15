package com.spring.its_here.domain.aihistory.controller;

import com.spring.its_here.domain.aihistory.controller.docs.AiHistoryApi;
import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryCursorResponseDto;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryResponseDto;
import com.spring.its_here.domain.aihistory.service.AiHistoryService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiHistoryController implements AiHistoryApi {

    private final AiHistoryService aiHistoryService;


    @GetMapping("/ai-histories/{aiHistoryId}")
    public ResponseEntity<ApiResponse<AiHistoryResponseDto>> getAiHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID aiHistoryId
    ) {
        AiHistoryResponseDto aiHistoryResponse = aiHistoryService.getAiHistory(aiHistoryId, userDetails.getUserId());
        return ResponseEntity.ok().body(ApiResponse.success("AI 기록 단일 조회 성공", aiHistoryResponse));
    }

    @GetMapping("/ai-histories")
    public ResponseEntity<ApiResponse<List<AiHistoryResponseDto>>> getAiHistories(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<AiHistoryResponseDto> aiHistories = aiHistoryService.getAiHistories(userDetails.getUserId());
        return ResponseEntity.ok().body(ApiResponse.success("AI 기록 전체 목록 조회 성공", aiHistories));
    }

    @GetMapping("/products/{productId}/ai-histories")
    public ResponseEntity<ApiResponse<AiHistoryCursorResponseDto>> getProductAiHistories(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid AiHistorySearchCondition condition,
            @PathVariable UUID productId
    ) {
        AiHistoryCursorResponseDto aiHistoryCursorResponseDto = aiHistoryService.searchAiHistories(condition, productId, userDetails.getUserId());
        return ResponseEntity.ok().body(ApiResponse.success("해당 상품 AI 기록 목록 조회 성공", aiHistoryCursorResponseDto));
    }

}
