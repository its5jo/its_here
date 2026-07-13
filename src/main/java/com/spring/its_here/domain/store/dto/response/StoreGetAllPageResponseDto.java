package com.spring.its_here.domain.store.dto.response;

import com.spring.its_here.global.response.OffsetPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record StoreGetAllPageResponseDto(
        List<StoreGetAllResponseDto> content,
        OffsetPageInfo pageInfo
) {

    public static StoreGetAllPageResponseDto from(Page<StoreGetAllResponseDto> page) {
        return new StoreGetAllPageResponseDto(
                page.getContent(),
                OffsetPageInfo.from(page)
        );
    }
}