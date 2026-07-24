package com.qms.qms.repository;

import com.qms.qms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByCodeIgnoreCase(String code);
}
