package com.qms.qms.dto.master;

import com.qms.qms.entity.Line;

public record LineResponse(Long id, Long factoryId, String code, String name) {
    public static LineResponse from(Line l) {
        return new LineResponse(l.getId(), l.getFactory().getId(), l.getCode(), l.getName());
    }
}
