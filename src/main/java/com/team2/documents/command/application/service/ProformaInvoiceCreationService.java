package com.team2.documents.command.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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

    /**
     * 초안 PI 를 직접 수정한다. 결재 없이 본인이 바로 편집 가능한 플로우.
     * DRAFT 상태가 아니면 거부한다.
     */
    public ProformaInvoice updateDraft(String piId, ProformaInvoiceCreateRequest request) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);
        if (!ProformaInvoiceStatus.DRAFT.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("초안 상태의 PI만 직접 수정할 수 있습니다.");
        }

        LocalDate issueDate = request.issueDate() == null ? LocalDate.now() : request.issueDate();
        String currencyCode = request.currencyCode();
        BigDecimal exchangeRate = request.exchangeRate();
        validateExchangeRate(currencyCode, exchangeRate);
        List<ProformaInvoiceItem> newItems = toEntities(request.items(), currencyCode, exchangeRate);
        BigDecimal totalAmount = calculateTotalAmount(request.totalAmount(), newItems, currencyCode, exchangeRate);

        proformaInvoice.setIssueDate(issueDate);
        if (request.clientId() != null) proformaInvoice.setClientId(request.clientId());
        if (request.currencyId() != null) proformaInvoice.setCurrencyId(request.currencyId());
        if (request.managerId() != null) proformaInvoice.setManagerId(request.managerId());
        proformaInvoice.setDeliveryDate(request.deliveryDate());
        proformaInvoice.setIncotermsCode(request.incotermsCode());
        proformaInvoice.setNamedPlace(request.namedPlace());
        proformaInvoice.setTotalAmount(totalAmount);
        proformaInvoice.setClientName(request.clientName());
        proformaInvoice.setClientAddress(request.clientAddress());
        proformaInvoice.setCountry(request.country());
        proformaInvoice.setCurrencyCode(currencyCode);
        proformaInvoice.setManagerName(request.managerName());
        proformaInvoice.setItemsSnapshot(serializeItemsSnapshot(newItems));

        // orphanRemoval 이 동작하려면 리스트 참조를 바꾸지 말고 내부를 갈아끼워야 한다.
        proformaInvoice.getItems().clear();
        proformaInvoice.getItems().addAll(newItems);

        ProformaInvoice saved = proformaInvoiceCommandService.save(proformaInvoice);
        docsSnapshotService.saveProformaInvoiceSnapshot(saved);
        documentRevisionHistoryService.recordProformaInvoiceEvent(
                saved.getPiId(),
                "DRAFT_UPDATE",
                request.userId(),
                saved.getStatus().name(),
                "초안 PI를 수정했습니다."
        );
        return saved;
    }

    public ProformaInvoice create(ProformaInvoiceCreateRequest request) {
        LocalDate issueDate = request.issueDate() == null ? LocalDate.now() : request.issueDate();
        String currencyCode = request.currencyCode();
        BigDecimal exchangeRate = request.exchangeRate();
        validateExchangeRate(currencyCode, exchangeRate);
        String piId = request.piId() == null || request.piId().isBlank()
                ? documentNumberGeneratorService.nextProformaInvoiceId()
                : request.piId();
        List<ProformaInvoiceItem> items = toEntities(request.items(), currencyCode, exchangeRate);
        BigDecimal totalAmount = calculateTotalAmount(request.totalAmount(), items, currencyCode, exchangeRate);

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

    private void validateExchangeRate(String currencyCode, BigDecimal exchangeRate) {
        String normalizedCurrencyCode = normalizeCurrencyCode(currencyCode);
        if ("KRW".equals(normalizedCurrencyCode)) {
            return;
        }
        if (exchangeRate == null) {
            throw new IllegalArgumentException("외화 PI 생성에는 exchangeRate가 필요합니다.");
        }
        if (exchangeRate.signum() <= 0) {
            throw new IllegalArgumentException("exchangeRate는 0보다 커야 합니다.");
        }
    }

    private List<ProformaInvoiceItem> toEntities(List<ProformaInvoiceItemCreateRequest> items,
                                                 String currencyCode,
                                                 BigDecimal exchangeRate) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> toEntity(item, currencyCode, exchangeRate))
                .toList();
    }

    private ProformaInvoiceItem toEntity(ProformaInvoiceItemCreateRequest item,
                                         String currencyCode,
                                         BigDecimal exchangeRate) {
        BigDecimal convertedUnitPrice = convertAmount(currencyCode, exchangeRate, item.unitPrice());
        BigDecimal convertedAmount = calculateItemAmount(item, currencyCode, exchangeRate, convertedUnitPrice);
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

    /** 총액은 서버에서 항상 재계산 — 클라이언트 요청값은 무시 (통화 환산 불일치/100배 오류 방지). */
    private BigDecimal calculateTotalAmount(BigDecimal requestedTotal,
                                            List<ProformaInvoiceItem> items,
                                            String currencyCode,
                                            BigDecimal exchangeRate) {
        return items.stream().map(ProformaInvoiceItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 라인 금액도 서버에서 항상 convertedUnitPrice × quantity 로 재계산. */
    private BigDecimal calculateItemAmount(ProformaInvoiceItemCreateRequest item,
                                           String currencyCode,
                                           BigDecimal exchangeRate,
                                           BigDecimal convertedUnitPrice) {
        BigDecimal unitPrice = convertedUnitPrice == null ? BigDecimal.ZERO : convertedUnitPrice;
        BigDecimal quantity = BigDecimal.valueOf(item.quantity() == null ? 0 : item.quantity());
        return unitPrice.multiply(quantity);
    }

    private BigDecimal convertAmount(String currencyCode, BigDecimal exchangeRate, BigDecimal amount) {
        return exchangeRateService.convertFromKrw(currencyCode, exchangeRate, amount);
    }

    private String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return "KRW";
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
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
