package com.spring.its_here.domain.address.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressCreateRequestDto(
        @NotBlank
        String address
) {
}
