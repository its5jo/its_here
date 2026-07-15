package com.spring.its_here.domain.store.repository;

import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepositoryCustom {

    Page<StoreGetAllResponseDto> getAllStores(String name, String category, String town, Pageable pageable);

}
