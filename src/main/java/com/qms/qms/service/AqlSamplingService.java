package com.qms.qms.service;

import com.qms.qms.entity.AqlSamplingPlan;
import com.qms.qms.entity.enums.AqlLevel;
import com.qms.qms.entity.enums.InspectionResult;
import com.qms.qms.exception.ResourceNotFoundException;
import com.qms.qms.repository.AqlSamplingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AqlSamplingService {

    private final AqlSamplingPlanRepository aqlSamplingPlanRepository;

    public AqlSamplingService(AqlSamplingPlanRepository aqlSamplingPlanRepository) {
        this.aqlSamplingPlanRepository = aqlSamplingPlanRepository;
    }

    public AqlSamplingPlan lookup(AqlLevel aqlLevel, Integer qtySize) {
        return aqlSamplingPlanRepository
                .findByAqlLevelAndQtyMinLessThanEqualAndQtyMaxGreaterThanEqual(aqlLevel, qtySize, qtySize)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sampling plan phù hợp"));
    }

    public InspectionResult evaluateResult(AqlSamplingPlan plan, int actualMajorDefects, int actualMinorDefects) {
        if (actualMajorDefects >= plan.getMajorReject()) {
            return InspectionResult.REJECTED;
        }
        if (actualMinorDefects >= plan.getMinorReject()) {
            return InspectionResult.PENDING;
        }
        return InspectionResult.PASS;
    }
}
