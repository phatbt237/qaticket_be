package com.qms.qms.dto.aql;

import com.qms.qms.entity.AqlSamplingPlan;
import com.qms.qms.entity.enums.AqlLevel;

public record AqlSamplingPlanResponse(
        AqlLevel aqlLevel,
        Integer qtyMin,
        Integer qtyMax,
        Integer samplingSize,
        Integer majorAccept,
        Integer majorReject,
        Integer minorAccept,
        Integer minorReject
) {
    public static AqlSamplingPlanResponse from(AqlSamplingPlan plan) {
        return new AqlSamplingPlanResponse(
                plan.getAqlLevel(),
                plan.getQtyMin(),
                plan.getQtyMax(),
                plan.getSamplingSize(),
                plan.getMajorAccept(),
                plan.getMajorReject(),
                plan.getMinorAccept(),
                plan.getMinorReject()
        );
    }
}
