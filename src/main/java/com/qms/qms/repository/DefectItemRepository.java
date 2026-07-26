package com.qms.qms.repository;

import com.qms.qms.entity.DefectItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefectItemRepository extends JpaRepository<DefectItem, Long> {
    List<DefectItem> findByDefectId(Long defectId);
}
