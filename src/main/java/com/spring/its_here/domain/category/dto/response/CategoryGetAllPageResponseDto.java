package com.spring.its_here.domain.category.dto.response;

import com.spring.its_here.domain.store.dto.response.StoreGetAllPageResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.global.response.OffsetPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record CategoryGetAllPageResponseDto(
        List<CategoryGetAllResponseDto> content,
        OffsetPageInfo pageInfo

) {

    public static CategoryGetAllPageResponseDto from(Page<CategoryGetAllResponseDto> page) {
        return new CategoryGetAllPageResponseDto(
                page.getContent(),
                OffsetPageInfo.from(page)
        );
    }

}
