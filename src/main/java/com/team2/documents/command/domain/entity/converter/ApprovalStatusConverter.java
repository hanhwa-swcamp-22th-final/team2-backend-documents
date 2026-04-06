package com.team2.documents.command.domain.entity.converter;

import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ApprovalStatusConverter implements AttributeConverter<ApprovalStatus, String> {

    @Override
    public String convertToDatabaseColumn(ApprovalStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ApprovalStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ApprovalStatus.fromDbValue(dbData);
    }
}
