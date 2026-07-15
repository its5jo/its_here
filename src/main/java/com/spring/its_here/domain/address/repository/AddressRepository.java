package com.spring.its_here.domain.address.repository;

import com.spring.its_here.domain.address.entity.Address;
import com.spring.its_here.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Boolean existsByUserIdAndAddressAndDeletedAtNull(Long userId, String address);

    Optional<Address> findByUserIdAndAddressAndDeletedAtNotNull(Long userId, String address);

    Optional<Address> findByIdAndDeletedAtNull(UUID userId);

    Page<Address> findAllByUserAndDeletedAtNull(UserEntity user, Pageable pageable);

    Page<Address> findByUserAndAddressContainingAndDeletedAtNull(UserEntity user, String address, Pageable pageable);
}
