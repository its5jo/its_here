package com.spring.its_here.infrastructure.ai;

import org.springframework.stereotype.Component;

@Component
public class ProductDescriptionPromptGenerator {

    public Prompt generate(String name, int price) {
        return new Prompt(
        """
                너는 음식점 상품 설명을 작성하는 마케팅 전문가다.
                다음 규칙을 반드시 준수한다.
                - 한국어로 작성한다.
                - 공백을 포함하여 100자 이하로 작성한다.
                - 이모지는 사용하지 않는다.
                - 상품 설명 외의 안내 문구나 따옴표는 출력하지 않는다.
                """,
                """
                다음 상품의 설명을 작성해줘.
                
                상품명: %s
                가격: %,d원
                """.formatted(name, price)
        );
    }
}