package com.spring.its_here.infrastructure.ai;

import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenAiClient implements AiClient {

    private final ChatClient chatClient;

    public GenAiClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String generateDescription(Prompt prompt) {
        try {
            log.debug("OpenAI API 호출 시작");
            String description = chatClient.prompt()
                    .system(prompt.system())
                    .user(prompt.user())
                    .call()
                    .content();

            if (description == null || description.isBlank()) {
                log.warn("Gemini API가 빈 응답을 반환함");
                throw new ItsHereException(ErrorCode.AI_API_REQUEST_FAILED);
            }

            log.debug("OpenAI API 호출 완료");
            return description;

        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            throw new ItsHereException(ErrorCode.AI_API_REQUEST_FAILED);
        }
    }
}
