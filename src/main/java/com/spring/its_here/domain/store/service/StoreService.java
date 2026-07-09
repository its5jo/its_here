package com.spring.its_here.domain.store.service;

import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.request.StoreUpdateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreUpdateResponseDto;
import com.spring.its_here.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public StoreCreateResponseDto createStore(StoreCreateRequestDto requestDto) {
        return null;
    }

    @Transactional(readOnly = true)
    public StoreGetOneResponseDto getOneStore(UUID storeId) {
        return null;
    }

    @Transactional(readOnly = true)
    public Page<StoreGetAllResponseDto> getAllStores(String name, String category, Pageable pageable) {
        return null;
    }

    @Transactional
    public StoreUpdateResponseDto updateStore(UUID storeId, StoreUpdateRequestDto requestDto) {
        return null;
    }

    @Transactional
    public void deleteStore(UUID storeId) {

    }
}
