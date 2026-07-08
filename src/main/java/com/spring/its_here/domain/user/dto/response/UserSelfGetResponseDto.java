package com.spring.its_here.domain.user.dto.response;

import com.spring.its_here.domain.user.enums.UserRole;

public record UserSelfGetResponseDto(
        String username,
        String nickname,
        UserRole role
) {
}
