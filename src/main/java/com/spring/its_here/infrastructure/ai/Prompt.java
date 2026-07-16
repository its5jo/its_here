package com.spring.its_here.infrastructure.ai;

public record Prompt(
        String system,
        String user
) {
    public String serialize() {
        return """
                [SYSTEM]
                %s
                [USER]
                %s
                """.formatted(system, user);
    }
}
