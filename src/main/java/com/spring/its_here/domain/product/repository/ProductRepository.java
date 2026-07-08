package com.spring.its_here.domain.product.repository;

import com.spring.its_here.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

//    List<Product> findAllByStoreId(UUID storeId); // TODO: Store 엔티티 생기면 주석 풀고 사용
}
