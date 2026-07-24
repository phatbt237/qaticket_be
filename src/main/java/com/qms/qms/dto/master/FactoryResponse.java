package com.qms.qms.dto.master;

import com.qms.qms.entity.Factory;

public record FactoryResponse(Long id, String code, String name, String address) {
    public static FactoryResponse from(Factory f) {
        return new FactoryResponse(f.getId(), f.getCode(), f.getName(), f.getAddress());
    }
}
