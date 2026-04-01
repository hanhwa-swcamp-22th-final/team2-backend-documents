package com.team2.documents.command.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceItemCreateRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.ProformaInvoiceItem;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

@Service
@Transactional
public class ProformaInvoiceCreationService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentJsonSupportService documentJsonSupportService;
    private final ObjectMapper objectMapper;

    public ProformaInvoiceCreationService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                          DocumentNumberGeneratorService documentNumberGeneratorService,
                                          DocumentJsonSupportService documentJsonSupportService,
                                          ObjectMapper objectMapper) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentJsonSupportService = documentJsonSupportService;
        this.objectMapper = objectMapper;
    }

    public ProformaInvoice create(ProformaInvoiceCreateRequest request) {
        String piId = request.piId() == null || request.piId().isBlank()
                ? documentNumberGeneratorService.nextProformaInvoiceId()
                : request.piId();
        LocalDateTime createdAt = LocalDateTime.now();

        List<ProformaInvoiceItem> items = toEntities(request.items());
        BigDecimal totalAmount = calculateTotalAmount(request.totalAmount(), items);

        ProformaInvoice proformaInvoice = new ProformaInvoice(
                piId,
                request.issueDate() == null ? LocalDate.now() : request.issueDate(),
                request.clientId() == null ? 0 : request.clientId(),
                request.currencyId() == null ? 0 : request.currencyId(),
                request.managerId() == null ? (request.userId() == null ? 0L : request.userId()) : request.managerId(),
                ProformaInvoiceStatus.DRAFT,
                request.deliveryDate(),
                request.incotermsCode(),
                request.namedPlace(),
                totalAmount,
                request.clientName(),
                request.clientAddress(),
                request.country(),
                request.currencyCode(),
                request.managerName(),
                null,
                null,
                null,
                null,
                null,
                null,
                serializeItemsSnapshot(items),
                documentJsonSupportService.emptyArray(),
                documentJsonSupportService.createRevisionHistory(
                        "CREATE",
                        request.userId() == null ? 0L : request.userId(),
                        ProformaInvoiceStatus.DRAFT.name(),
                        "PI 초안을 생성했습니다.",
                        createdAt
                ),
                items
        );

        return proformaInvoiceCommandService.save(proformaInvoice);
    }

    private List<ProformaInvoiceItem> toEntities(List<ProformaInvoiceItemCreateRequest> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> new ProformaInvoiceItem(
                        item.itemId(),
                        item.itemName() == null ? "" : item.itemName(),
                        item.quantity(),
                        item.unit(),
                        item.unitPrice(),
                        calculateItemAmount(item),
                        item.remark()
                ))
                .toList();
    }

    private BigDecimal calculateTotalAmount(BigDecimal requestedTotal, List<ProformaInvoiceItem> items) {
        if (requestedTotal != null) {
            return requestedTotal;
        }
        return items.stream().map(ProformaInvoiceItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemAmount(ProformaInvoiceItemCreateRequest item) {
        if (item.amount() != null) {
            return item.amount();
        }
        BigDecimal unitPrice = item.unitPrice() == null ? BigDecimal.ZERO : item.unitPrice();
        BigDecimal quantity = BigDecimal.valueOf(item.quantity() == null ? 0 : item.quantity());
        return unitPrice.multiply(quantity);
    }

    private String serializeItemsSnapshot(List<ProformaInvoiceItem> items) {
        List<Map<String, Object>> snapshot = items.stream()
                .map(item -> Map.<String, Object>of(
                        "itemId", item.getItemId() == null ? 0 : item.getItemId(),
                        "itemName", item.getItemName(),
                        "quantity", item.getQuantity(),
                        "unit", item.getUnit() == null ? "" : item.getUnit(),
                        "unitPrice", item.getUnitPrice(),
                        "amount", item.getAmount(),
                        "remark", item.getRemark() == null ? "" : item.getRemark()
                ))
                .toList();
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("PI 스냅샷 JSON 생성에 실패했습니다.", exception);
        }
    }
}
