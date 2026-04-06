package com.team2.documents.command.domain.entity.converter;

import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PurchaseOrderStatusConverter implements AttributeConverter<PurchaseOrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(PurchaseOrderStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public PurchaseOrderStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PurchaseOrderStatus.fromDbValue(dbData);
    }
}
