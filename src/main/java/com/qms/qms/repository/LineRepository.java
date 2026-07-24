package com.qms.qms.repository;

import com.qms.qms.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {
    List<Line> findByFactoryId(Long factoryId);
}
