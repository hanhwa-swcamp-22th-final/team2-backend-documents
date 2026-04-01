package com.team2.documents.command.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProformaInvoiceCreateRequest(
        String piId,
        LocalDate issueDate,
        Integer clientId,
        Integer currencyId,
        Long managerId,
        LocalDate deliveryDate,
        String incotermsCode,
        String namedPlace,
        BigDecimal totalAmount,
        String clientName,
        String clientAddress,
        String country,
        String currencyCode,
        String managerName,
        Long userId,
        List<ProformaInvoiceItemCreateRequest> items
) {
}
