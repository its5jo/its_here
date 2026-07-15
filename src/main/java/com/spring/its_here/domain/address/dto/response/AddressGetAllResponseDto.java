package com.spring.its_here.domain.address.dto.response;

import com.spring.its_here.domain.address.entity.Address;
import com.spring.its_here.global.response.OffsetPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record AddressGetAllResponseDto(
        List<AddressGetResponseDto> content,
        OffsetPageInfo pageInfo
) {

    public static AddressGetAllResponseDto from(
            List<AddressGetResponseDto> content,
            Page<Address> page
    ) {
        return new AddressGetAllResponseDto(
                content,
                OffsetPageInfo.from(page)
        );
    }
}
