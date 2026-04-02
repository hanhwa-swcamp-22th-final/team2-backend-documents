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
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderItemCreateRequest;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.PurchaseOrderItem;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class PurchaseOrderCreationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentLinkService documentLinkService;
    private final DocsSnapshotService docsSnapshotService;
    private final ObjectMapper objectMapper;

    public PurchaseOrderCreationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                        DocumentNumberGeneratorService documentNumberGeneratorService,
                                        DocumentLinkService documentLinkService,
                                        DocsSnapshotService docsSnapshotService,
                                        ObjectMapper objectMapper) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.docsSnapshotService = docsSnapshotService;
        this.objectMapper = objectMapper;
    }

    public PurchaseOrderStatus determineInitialStatus(Long userId) {
        return PurchaseOrderStatus.DRAFT;
    }

    public PurchaseOrder create(PurchaseOrderCreateRequest request) {
        PurchaseOrderStatus initialStatus = PurchaseOrderStatus.DRAFT;
        String poId = request.poId() == null || request.poId().isBlank()
                ? documentNumberGeneratorService.nextPurchaseOrderId()
                : request.poId();
        LocalDateTime createdAt = LocalDateTime.now();

        List<PurchaseOrderItem> items = toEntities(request.items());
        BigDecimal totalAmount = calculateTotalAmount(request.totalAmount(), items);

        PurchaseOrder purchaseOrder = new PurchaseOrder(
                poId,
                request.piId(),
                request.issueDate() == null ? LocalDate.now() : request.issueDate(),
                request.clientId() == null ? 0 : request.clientId(),
                request.currencyId() == null ? 0 : request.currencyId(),
                resolveManagerId(request),
                initialStatus,
                request.deliveryDate(),
                request.incotermsCode(),
                request.namedPlace(),
                request.sourceDeliveryDate(),
                Boolean.TRUE.equals(request.deliveryDateOverride()),
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
                serializeLinkedDocuments(),
                serializeRevisionHistory(request.userId(), initialStatus, createdAt),
                items
        );

        PurchaseOrder saved = purchaseOrderCommandService.save(purchaseOrder);
        docsSnapshotService.savePurchaseOrderSnapshot(saved);
        if (saved.getPiId() != null && !saved.getPiId().isBlank()) {
            documentLinkService.linkPurchaseOrderToProformaInvoice(saved.getPoId(), saved.getPiId());
        }
        return saved;
    }

    public void create(Long userId) {
        create(new PurchaseOrderCreateRequest(userId));
    }

    private Long resolveManagerId(PurchaseOrderCreateRequest request) {
        if (request.managerId() != null) {
            return request.managerId();
        }
        if (request.userId() != null) {
            return request.userId();
        }
        return 0L;
    }

    private List<PurchaseOrderItem> toEntities(List<PurchaseOrderItemCreateRequest> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> new PurchaseOrderItem(
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

    private BigDecimal calculateTotalAmount(BigDecimal requestedTotal, List<PurchaseOrderItem> items) {
        if (requestedTotal != null) {
            return requestedTotal;
        }
        return items.stream()
                .map(PurchaseOrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemAmount(PurchaseOrderItemCreateRequest item) {
        if (item.amount() != null) {
            return item.amount();
        }
        BigDecimal unitPrice = item.unitPrice() == null ? BigDecimal.ZERO : item.unitPrice();
        BigDecimal quantity = BigDecimal.valueOf(item.quantity() == null ? 0 : item.quantity());
        return unitPrice.multiply(quantity);
    }

    private String serializeItemsSnapshot(List<PurchaseOrderItem> items) {
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
        return writeJson(snapshot);
    }

    private String serializeLinkedDocuments() {
        return writeJson(List.of());
    }

    private String serializeRevisionHistory(Long userId, PurchaseOrderStatus status, LocalDateTime createdAt) {
        return writeJson(List.of(Map.of(
                "action", "CREATE",
                "actorUserId", userId == null ? 0L : userId,
                "status", status.name(),
                "at", createdAt.toString()
        )));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("PO 스냅샷 JSON 생성에 실패했습니다.", exception);
        }
    }
}
