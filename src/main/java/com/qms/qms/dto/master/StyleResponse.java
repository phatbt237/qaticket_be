package com.qms.qms.dto.master;

import com.qms.qms.entity.Style;

public record StyleResponse(Long id, String code, String name) {
    public static StyleResponse from(Style s) {
        return new StyleResponse(s.getId(), s.getCode(), s.getName());
    }
}
