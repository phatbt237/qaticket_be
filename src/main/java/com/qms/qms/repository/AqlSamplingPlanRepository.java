package com.qms.qms.repository;

import com.qms.qms.entity.AqlSamplingPlan;
import com.qms.qms.entity.enums.AqlLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AqlSamplingPlanRepository extends JpaRepository<AqlSamplingPlan, Long> {

    Optional<AqlSamplingPlan> findByAqlLevelAndQtyMinLessThanEqualAndQtyMaxGreaterThanEqual(
            AqlLevel aqlLevel, Integer qtySizeMin, Integer qtySizeMax);
}
