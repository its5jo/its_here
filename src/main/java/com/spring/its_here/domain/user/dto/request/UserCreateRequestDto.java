package com.spring.its_here.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequestDto(
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 10, message = "아이디는 4자 이상 10자 이하입니다.")
        @Pattern(
                regexp = "^[a-z0-9]+$",
                message = "아이디는 영문 소문자와 숫자만 사용할 수 있습니다."
        )
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$",
                message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하입니다.")
        String nickname
) {
}
