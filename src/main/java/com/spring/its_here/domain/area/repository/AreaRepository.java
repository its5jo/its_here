package com.spring.its_here.domain.area.repository;

import com.spring.its_here.domain.area.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
    boolean existsByCityAndDistrictAndTown(
            String city,
            String district,
            String town
    );
}
