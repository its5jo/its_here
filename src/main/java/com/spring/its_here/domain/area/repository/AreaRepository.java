package com.spring.its_here.domain.area.repository;

import com.spring.its_here.domain.area.entity.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
    boolean existsByCityAndDistrictAndTown(
            String city,
            String district,
            String town
    );

    @Query("""
        SELECT a
        FROM Area a
        WHERE a.deletedAt IS NULL
        AND (:city IS NULL OR a.city = :city)
        AND (:district IS NULL OR a.district = :district)
        AND (:town IS NULL OR a.town = :town)
        AND (:hasAvailable IS NULL OR a.hasAvailable = :hasAvailable)
""")
    Page<Area> searchAreas(
            @Param("city") String city,
            @Param("district") String district,
            @Param("town") String town,
            @Param("hasAvailable") Boolean hasAvailable,
            Pageable pageable
    );

    Optional<Area> findByIdAndDeletedAtIsNull(UUID areaId);
}