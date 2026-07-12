package com.spring.its_here.domain.store.dto.response;

import com.spring.its_here.global.response.PageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record StoreGetAllPageResponseDto(
        List<StoreGetAllResponseDto> content,
        PageInfo pageInfo
) {

    public static StoreGetAllPageResponseDto from(Page<StoreGetAllResponseDto> page) {
        return new StoreGetAllPageResponseDto(
                page.getContent(),
                PageInfo.from(page)
        );
    }
}