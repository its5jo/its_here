package com.spring.its_here.infrastructure.ai;

import org.springframework.stereotype.Component;

@Component
public class ProductDescriptionPromptGenerator {

    public String generate(String name, int price) {
        return """
                다음 상품의 설명을 작성해줘.
                상품명: %s
                가격: %d원
                답변을 최대한 간결하게 50자 이하로 작성해줘.
                """.formatted(
                name,
                price
        );
    }
}