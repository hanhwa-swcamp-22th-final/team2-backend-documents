package com.team2.documents.command.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderItemCreateRequest;
import com.team2.documents.command.domain.entity.Collection;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.PurchaseOrderItem;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.domain.repository.CollectionRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;

@Service
@Transactional
public class PurchaseOrderCreationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentLinkService documentLinkService;
    private final DocsSnapshotService docsSnapshotService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ShipmentRepository shipmentRepository;
    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

    public PurchaseOrderCreationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                        DocumentNumberGeneratorService documentNumberGeneratorService,
                                        DocumentLinkService documentLinkService,
                                        DocsSnapshotService docsSnapshotService,
                                        DocumentRevisionHistoryService documentRevisionHistoryService,
                                        ShipmentRepository shipmentRepository,
                                        CollectionRepository collectionRepository,
                                        ObjectMapper objectMapper) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.docsSnapshotService = docsSnapshotService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.shipmentRepository = shipmentRepository;
        this.collectionRepository = collectionRepository;
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
                items
        );

        PurchaseOrder saved = purchaseOrderCommandService.save(purchaseOrder);
        createInitialViews(saved);
        docsSnapshotService.savePurchaseOrderSnapshot(saved);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                saved.getPoId(),
                "CREATE",
                request.userId(),
                saved.getStatus().name(),
                "PO 초안을 생성했습니다."
        );
        if (saved.getPiId() != null && !saved.getPiId().isBlank()) {
            documentLinkService.linkPurchaseOrderToProformaInvoice(saved.getPoId(), saved.getPiId());
        }
        return saved;
    }

    private void createInitialViews(PurchaseOrder purchaseOrder) {
        shipmentRepository.save(new Shipment(
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                ShipmentStatus.READY
        ));

        Collection collection = new Collection(
                null,
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                purchaseOrder.getClientId() == null ? null : purchaseOrder.getClientId().longValue(),
                purchaseOrder.getClientName(),
                purchaseOrder.getTotalAmount(),
                BigDecimal.ZERO,
                purchaseOrder.getTotalAmount(),
                purchaseOrder.getCurrencyCode(),
                "미수금",
                null,
                null,
                null
        );
        collection.setManagerId(purchaseOrder.getManagerId());
        collection.setCurrencyId(purchaseOrder.getCurrencyId());
        collection.setCollectionIssueDate(purchaseOrder.getIssueDate());
        collectionRepository.save(collection);
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

    /** 총액은 서버에서 항상 재계산 — 클라이언트 파라미터 신뢰하지 않음 (100배 차이 같은 케이스 방지). */
    private BigDecimal calculateTotalAmount(BigDecimal requestedTotal, List<PurchaseOrderItem> items) {
        return items.stream()
                .map(PurchaseOrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 라인 금액도 서버에서 항상 unitPrice × quantity 로 재계산. */
    private BigDecimal calculateItemAmount(PurchaseOrderItemCreateRequest item) {
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

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("PO 스냅샷 JSON 생성에 실패했습니다.", exception);
        }
    }
}
