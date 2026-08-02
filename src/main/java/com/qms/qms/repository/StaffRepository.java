package com.qms.qms.repository;

import com.qms.qms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {
    Optional<Staff> findByCodeIgnoreCase(String code);
}
