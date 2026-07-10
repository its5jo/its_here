package com.spring.its_here.domain.area.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AreaCreateRequestDto(
        @NotBlank(message = "시/도는 필수값입니다.")
        String city,
        @NotBlank(message = "시/군/구는 필수값입니다.")
        String district,
        @NotBlank(message = "행정동은 필수값입니다.")
        String town
) {
}
