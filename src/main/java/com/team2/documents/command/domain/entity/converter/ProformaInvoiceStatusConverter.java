package com.team2.documents.command.domain.entity.converter;

import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProformaInvoiceStatusConverter implements AttributeConverter<ProformaInvoiceStatus, String> {

    @Override
    public String convertToDatabaseColumn(ProformaInvoiceStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ProformaInvoiceStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ProformaInvoiceStatus.fromDbValue(dbData);
    }
}
