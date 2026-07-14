package com.spring.its_here.domain.aihistory.controller;

import com.spring.its_here.domain.aihistory.controller.docs.AiHistoryApi;
import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;
import com.spring.its_here.domain.aihistory.service.AiHistoryService;
import com.spring.its_here.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiHistoryController implements AiHistoryApi {

    private final AiHistoryService aiHistoryService;


    @GetMapping("/ai-histories/{aiHistoryId}")
    public ResponseEntity<ApiResponse<Void>> getAiHistory(
            @PathVariable UUID aiHistoryId
    ) {
        return ResponseEntity.ok().body(ApiResponse.success("AI 기록 단일 조회 성공", null));
    }

    @GetMapping("/ai-histories")
    public ResponseEntity<ApiResponse<Void>> getAiHistories() {
        return ResponseEntity.ok().body(ApiResponse.success("AI 기록 전체 목록 조회 성공", null));
    }

    @GetMapping("/products/{productId}/ai-histories")
    public ResponseEntity<ApiResponse<Void>> getProductAiHistories(
            @Valid AiHistorySearchCondition condition,
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok().body(ApiResponse.success("해당 상품 AI 기록 목록 조회 성공", null));
    }

}
