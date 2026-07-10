package com.spring.its_here.domain.store.service;

import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.request.StoreUpdateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreUpdateResponseDto;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import com.spring.its_here.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;
    private final AuthenticationFacade authenticationFacade;

    @PreAuthorize("hasAnyAuthority('OWNER','MANAGER','MASTER')")
    @Transactional
    public StoreCreateResponseDto createStore(
            CustomUserDetails userDetails, StoreCreateRequestDto requestDto) {

        //UserEntity u1 = authenticationFacade.getCurrentUser().getUser();

        validateStoreCreate(userDetails, requestDto);

        Category category = findCategoryOrThrow(requestDto.categoryId());
        Area area = findAreaOrThrow(requestDto.areaId());

        Store store = Store.createStore(requestDto.name(), requestDto.address(),
                userDetails.getUser(), category, area,
                requestDto.hasOpen(), requestDto.openAt(), requestDto.closedAt());

        Store newStore = storeRepository.save(store);

        return new StoreCreateResponseDto(newStore.getId());
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

    private void validateStoreCreate(
            CustomUserDetails userDetails,
            StoreCreateRequestDto requestDto) {

        existsDuplicateStoreName(requestDto.name());

        existsUsersStore(userDetails.getUserId());
    }

    private void existsDuplicateStoreName(String name) {
        if(storeRepository.existsByNameAndDeletedAtIsNull(name)){
            throw new ItsHereException(ErrorCode.DUPLICATE_STORE_NAME);
        }
    }

    // 이거는 가게 주인일 때만 검증해야하나..? 왜냐면 관리자는 여러 개의 가게를 생성할 수 있어야 맞는데(근데 문제는 1대1임)
    private void existsUsersStore(Long userId){
        if(storeRepository.existsUserIdAndDeletedAtIsNull(userId)){
            throw new ItsHereException(ErrorCode.USER_ALREADY_HAS_STORE);
        }
    }

    private Category findCategoryOrThrow(UUID categoryId){
        return categoryRepository.findByIdAndHasDeletedFalse(categoryId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Area findAreaOrThrow(UUID areaId){
        return areaRepository.findByIdAndHasDeletedFalse(areaId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.AREA_NOT_FOUND));
    }
}