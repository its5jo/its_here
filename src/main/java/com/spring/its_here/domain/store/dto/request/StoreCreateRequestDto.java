package com.spring.its_here.domain.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.UUID;

public record StoreCreateRequestDto(

        @NotBlank(message = "가게 이름은 필수입니다.")
        @Size(max = 30, message = "가게 이름은 30자 이하입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9 ]+$", message = "가게 이름은 한글, 영문, 숫자, 띄어쓰기만 입력 가능합니다.")
        String name,

        @NotBlank(message = "가게 주소는 필수입니다.")
        @Size(max = 255, message = "가게 주소는 255자 이하입니다.")
        String address,

        @NotNull(message = "가게 오픈 상태는 필수입니다.")
        Boolean hasOpen,

        @NotNull(message = "지역 id는 필수입니다.")
        UUID areaId,

        @NotNull(message = "카테고리 id는 필수입니다.")
        UUID categoryId,

        // 9:20, 09:20, 11:20 처리 가능
        @JsonFormat(pattern = "H:mm")
        LocalTime openAt,

        @JsonFormat(pattern = "H:mm")
        LocalTime closedAt

) {

}
