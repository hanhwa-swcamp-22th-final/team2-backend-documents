package com.team2.documents.command.domain.entity.converter;

import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ApprovalRequestTypeConverter implements AttributeConverter<ApprovalRequestType, String> {

    @Override
    public String convertToDatabaseColumn(ApprovalRequestType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ApprovalRequestType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ApprovalRequestType.fromDbValue(dbData);
    }
}
