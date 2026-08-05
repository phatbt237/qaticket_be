package com.qms.qms.repository;

import com.qms.qms.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DefectRepository extends JpaRepository<Defect, Long> {
    Optional<Defect> findByCodeIgnoreCase(String code);
}
