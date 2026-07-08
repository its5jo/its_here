package com.spring.its_here.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequestDto(
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(max = 30, message = "카테고리 이름은 30자 이하입니다.")
        @Pattern(regexp = "^[가-힣]+$", message = "카테고리 이름은 한글만 입력 가능합니다.")
        String name,

        @NotNull(message = "카테고리 숨김 여부는 필수입니다.")
        Boolean hasHidden
) {

}
