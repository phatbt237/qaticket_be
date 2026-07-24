package com.qms.qms.repository;

import com.qms.qms.entity.ProductionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionGroupRepository extends JpaRepository<ProductionGroup, Long> {
    List<ProductionGroup> findByLineId(Long lineId);
}
