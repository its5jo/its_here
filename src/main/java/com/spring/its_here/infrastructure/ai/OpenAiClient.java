package com.spring.its_here.infrastructure.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient implements AiClient {

    @Override
    public String generateDescription(String prompt) {
        log.debug("OpenAI API 호출 시작");
        // TODO: ai 사용 로직 추가
        log.debug("OpenAI API 호출 완료");
        return "";
    }
}
