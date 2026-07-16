package com.spring.its_here.domain.area.entity;


import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_area",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_area_city_district_town",
                        columnNames = {"city", "district", "town"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Area extends BaseDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "town", nullable = false)
    private String town;

    @Column(name = "has_available", nullable = false)
    private boolean hasAvailable = false;

    public static Area create(
            String city,
            String district,
            String town
    ) {
        Area area = new Area();
        area.city = city;
        area.district = district;
        area.town = town;
        return area;
    }

    public void updatedArea(
            String city,
            String district,
            String town,
            boolean hasAvailable
    ) {
        this.city = city;
        this.district = district;
        this.town = town;
        this.hasAvailable = hasAvailable;
    }
}
