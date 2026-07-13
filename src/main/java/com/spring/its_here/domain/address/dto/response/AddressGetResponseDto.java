package com.spring.its_here.domain.address.dto.response;

import java.util.UUID;

public record AddressGetResponseDto(
        UUID addressId,
        String address
) {
}
