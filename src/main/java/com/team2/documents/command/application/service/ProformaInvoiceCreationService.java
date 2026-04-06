package com.team2.documents.command.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final DocsSnapshotService docsSnapshotService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ExchangeRateService exchangeRateService;
    private final ObjectMapper objectMapper;

    public ProformaInvoiceCreationService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                          DocumentNumberGeneratorService documentNumberGeneratorService,
                                          DocsSnapshotService docsSnapshotService,
                                          DocumentRevisionHistoryService documentRevisionHistoryService,
                                          ExchangeRateService exchangeRateService,
                                          ObjectMapper objectMapper) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.docsSnapshotService = docsSnapshotService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.exchangeRateService = exchangeRateService;
        this.objectMapper = objectMapper;
    }

    public ProformaInvoice create(ProformaInvoiceCreateRequest request) {
        LocalDate issueDate = request.issueDate() == null ? LocalDate.now() : request.issueDate();
        String currencyCode = request.currencyCode();
        String piId = request.piId() == null || request.piId().isBlank()
                ? documentNumberGeneratorService.nextProformaInvoiceId()
                : request.piId();
        List<ProformaInvoiceItem> items = toEntities(request.items(), issueDate, currencyCode);
        BigDecimal totalAmount = calculateTotalAmount(request.totalAmount(), items, issueDate, currencyCode);

        ProformaInvoice proformaInvoice = new ProformaInvoice(
                piId,
                issueDate,
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
                currencyCode,
                request.managerName(),
                null,
                null,
                null,
                null,
                null,
                null,
                serializeItemsSnapshot(items),
                "[]",
                items
        );

        ProformaInvoice saved = proformaInvoiceCommandService.save(proformaInvoice);
        docsSnapshotService.saveProformaInvoiceSnapshot(saved);
        documentRevisionHistoryService.recordProformaInvoiceEvent(
                saved.getPiId(),
                "CREATE",
                request.userId(),
                saved.getStatus().name(),
                "PI 초안을 생성했습니다."
        );
        return saved;
    }

    private List<ProformaInvoiceItem> toEntities(List<ProformaInvoiceItemCreateRequest> items,
                                                 LocalDate issueDate,
                                                 String currencyCode) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> toEntity(item, issueDate, currencyCode))
                .toList();
    }

    private ProformaInvoiceItem toEntity(ProformaInvoiceItemCreateRequest item,
                                         LocalDate issueDate,
                                         String currencyCode) {
        BigDecimal convertedUnitPrice = convertAmount(issueDate, currencyCode, item.unitPrice());
        BigDecimal convertedAmount = calculateItemAmount(item, issueDate, currencyCode, convertedUnitPrice);
        return new ProformaInvoiceItem(
                item.itemId(),
                item.itemName() == null ? "" : item.itemName(),
                item.quantity(),
                item.unit(),
                convertedUnitPrice,
                convertedAmount,
                item.remark()
        );
    }

    private BigDecimal calculateTotalAmount(BigDecimal requestedTotal,
                                            List<ProformaInvoiceItem> items,
                                            LocalDate issueDate,
                                            String currencyCode) {
        if (requestedTotal != null) {
            return convertAmount(issueDate, currencyCode, requestedTotal);
        }
        return items.stream().map(ProformaInvoiceItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemAmount(ProformaInvoiceItemCreateRequest item,
                                           LocalDate issueDate,
                                           String currencyCode,
                                           BigDecimal convertedUnitPrice) {
        if (item.amount() != null) {
            return convertAmount(issueDate, currencyCode, item.amount());
        }
        BigDecimal unitPrice = convertedUnitPrice == null ? BigDecimal.ZERO : convertedUnitPrice;
        BigDecimal quantity = BigDecimal.valueOf(item.quantity() == null ? 0 : item.quantity());
        return unitPrice.multiply(quantity);
    }

    private BigDecimal convertAmount(LocalDate issueDate, String currencyCode, BigDecimal amount) {
        return exchangeRateService.convertFromKrw(issueDate, currencyCode, amount);
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
