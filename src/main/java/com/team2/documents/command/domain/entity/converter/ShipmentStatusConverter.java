package com.team2.documents.command.domain.entity.converter;

import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShipmentStatusConverter implements AttributeConverter<ShipmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(ShipmentStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ShipmentStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ShipmentStatus.fromDbValue(dbData);
    }
}
