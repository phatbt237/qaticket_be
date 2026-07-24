package com.qms.qms.repository;

import com.qms.qms.entity.GarmentLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GarmentLocationRepository extends JpaRepository<GarmentLocation, Long> {
    List<GarmentLocation> findByGarmentTypeId(Long garmentTypeId);
}
