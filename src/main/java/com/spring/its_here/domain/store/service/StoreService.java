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
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @PreAuthorize("hasAnyAuthority('OWNER','MANAGER','MASTER')")
    @Transactional
    public StoreCreateResponseDto createStore(
            CustomUserDetails userDetails, StoreCreateRequestDto requestDto) {

        validateStoreCreate(userDetails, requestDto);

        Category category = findCategoryByIdAndNotDeleted(requestDto.categoryId());
        Area area = findAreaByIdAndNotDeleted(requestDto.areaId());

        Store store = Store.createStore(requestDto.name(), requestDto.address(),
                userDetails.getUser(), category, area,
                requestDto.hasOpen(), requestDto.openAt(), requestDto.closedAt());

        Store newStore = storeRepository.save(store);

        return StoreCreateResponseDto.from(newStore.getId());
    }

    @PreAuthorize("hasAnyAuthority('OWNER','MANAGER','MASTER')")
    @Transactional(readOnly = true)
    public StoreGetOneResponseDto getOneStore(CustomUserDetails userDetails, UUID storeId) {

        Store store = findStoreByIdAndNotDeleted(storeId);
        validateStoreOwner(userDetails, store.getUser().getId());

        Category category = store.getCategory();
        Area area = store.getArea();

        return StoreGetOneResponseDto.from(store, area, category);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    @Transactional(readOnly = true)
    public Page<StoreGetAllResponseDto> getAllStores(String name, String category, Pageable pageable) {
        Pageable newPageable = validatePageable(pageable);
        return storeRepository.getAllStores(name, category, newPageable);
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

    private void validateStoreOwner(CustomUserDetails userDetails, Long storeOwnerId){
        UserEntity user = userDetails.getUser();

        if(user.getRole() == UserRole.OWNER && !user.getId().equals(storeOwnerId)){
            throw new ItsHereException(ErrorCode.STORE_NOT_OWNED);
        }
    }

    private Pageable validatePageable(Pageable pageable) {
        int size = pageable.getPageSize();

        if (size == 10 || size == 30 || size == 50) {
            return pageable;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                10,
                pageable.getSort()
        );
    }

    private void existsDuplicateStoreName(String name) {
        if(storeRepository.existsByNameAndDeletedAtIsNull(name)){
            throw new ItsHereException(ErrorCode.STORE_NAME_DUPLICATE);
        }
    }

    private void existsUsersStore(Long userId){
        if(storeRepository.existsByUserIdAndDeletedAtIsNull(userId)){
            throw new ItsHereException(ErrorCode.STORE_ALREADY_REGISTERED);
        }
    }

    private Category findCategoryById(UUID storeId){
        return categoryRepository.findById(storeId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Area findAreaById(UUID areaId){
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.AREA_NOT_FOUND));
    }

    private Category findCategoryByIdAndNotDeleted(UUID categoryId){
        return categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Area findAreaByIdAndNotDeleted(UUID areaId){
        return areaRepository.findByIdAndDeletedAtIsNull(areaId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.AREA_NOT_FOUND));
    }

    private Store findStoreByIdAndNotDeleted(UUID storeId) {
        return storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));
    }
}