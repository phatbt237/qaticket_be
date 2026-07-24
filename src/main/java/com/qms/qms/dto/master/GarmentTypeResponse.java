package com.qms.qms.dto.master;

import com.qms.qms.entity.GarmentType;

public record GarmentTypeResponse(Long id, String name) {
    public static GarmentTypeResponse from(GarmentType g) {
        return new GarmentTypeResponse(g.getId(), g.getName());
    }
}
