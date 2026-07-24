package com.qms.qms.dto.master;

import com.qms.qms.entity.GarmentLocation;

public record GarmentLocationResponse(Long id, Long garmentTypeId, String name) {
    public static GarmentLocationResponse from(GarmentLocation g) {
        return new GarmentLocationResponse(g.getId(), g.getGarmentType().getId(), g.getName());
    }
}
