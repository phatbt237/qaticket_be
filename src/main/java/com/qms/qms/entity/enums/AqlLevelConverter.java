package com.qms.qms.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AqlLevelConverter implements AttributeConverter<AqlLevel, String> {

    @Override
    public String convertToDatabaseColumn(AqlLevel attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public AqlLevel convertToEntityAttribute(String dbData) {
        return dbData != null ? AqlLevel.fromCode(dbData) : null;
    }
}
