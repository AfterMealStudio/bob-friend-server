package com.example.bob_friend.model.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
class SexConverter implements AttributeConverter<Sex, String> {

    @Override
    public String convertToDatabaseColumn(Sex attribute) {
        return attribute.name().toUpperCase();
    }

    @Override
    public Sex convertToEntityAttribute(String dbData) {
        return Sex.valueOf(dbData.toUpperCase());
    }
}
