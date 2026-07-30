package com.qms.qms.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AqlLevel {
    AQL_1_5("1.5"),
    AQL_2_5("2.5");

    private final String code;

    AqlLevel(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static AqlLevel fromCode(String code) {
        for (AqlLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid AQL level: " + code);
    }
}
