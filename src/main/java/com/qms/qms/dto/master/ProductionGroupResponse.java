package com.qms.qms.dto.master;

import com.qms.qms.entity.ProductionGroup;

public record ProductionGroupResponse(Long id, Long lineId, String name) {
    public static ProductionGroupResponse from(ProductionGroup g) {
        return new ProductionGroupResponse(g.getId(), g.getLine().getId(), g.getName());
    }
}
