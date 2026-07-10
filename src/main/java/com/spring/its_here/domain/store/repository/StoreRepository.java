package com.spring.its_here.domain.store.repository;

import com.spring.its_here.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {

    boolean existsByNameAndDeletedAtIsNull(String name);
    boolean existsByUserIdAndDeletedAtIsNull(Long userId);

}
