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
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.PurchaseOrderItem;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.domain.repository.CollectionRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

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
    private final ProformaInvoiceRepository proformaInvoiceRepository;
    private final ObjectMapper objectMapper;
    private final UserPositionRepository userPositionRepository;

    public PurchaseOrderCreationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                        DocumentNumberGeneratorService documentNumberGeneratorService,
                                        DocumentLinkService documentLinkService,
                                        DocsSnapshotService docsSnapshotService,
                                        DocumentRevisionHistoryService documentRevisionHistoryService,
                                        ShipmentRepository shipmentRepository,
                                        CollectionRepository collectionRepository,
                                        ProformaInvoiceRepository proformaInvoiceRepository,
                                        ObjectMapper objectMapper,
                                        UserPositionRepository userPositionRepository) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.docsSnapshotService = docsSnapshotService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.shipmentRepository = shipmentRepository;
        this.collectionRepository = collectionRepository;
        this.proformaInvoiceRepository = proformaInvoiceRepository;
        this.objectMapper = objectMapper;
        this.userPositionRepository = userPositionRepository;
    }

    public PurchaseOrderStatus determineInitialStatus(Long userId) {
        return PurchaseOrderStatus.DRAFT;
    }

    /**
     * PO 직접 수정. 결재 없이 본인이 바로 편집.
     * - STAFF: DRAFT 만 허용.
     * - MANAGER(팀장/ADMIN): DRAFT + CONFIRMED 모두 허용. 상태 유지.
     * PI 재연결도 허용하되 새 PI 가 CONFIRMED 인지 확인.
     */
    public PurchaseOrder updateDraft(String poId, PurchaseOrderCreateRequest request) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        PurchaseOrderStatus status = purchaseOrder.getStatus();

        boolean isManager = request.userId() != null
                && userPositionRepository.findPositionLevelByUserId(request.userId())
                        .map(PositionLevel.MANAGER::equals)
                        .orElse(false);
        boolean allowed = PurchaseOrderStatus.DRAFT.equals(status)
                || (isManager && PurchaseOrderStatus.CONFIRMED.equals(status));
        if (!allowed) {
            throw new IllegalStateException(isManager
                    ? "초안 또는 확정 상태의 PO만 직접 수정할 수 있습니다."
                    : "초안 상태의 PO만 직접 수정할 수 있습니다.");
        }
        validateLinkedProformaInvoice(request.piId());

        List<PurchaseOrderItem> newItems = toEntities(request.items());
        BigDecimal totalAmount = calculateTotalAmount(request.totalAmount(), newItems);

        purchaseOrder.setPiId(request.piId());
        purchaseOrder.setIssueDate(request.issueDate() == null ? LocalDate.now() : request.issueDate());
        if (request.clientId() != null) purchaseOrder.setClientId(request.clientId());
        if (request.currencyId() != null) purchaseOrder.setCurrencyId(request.currencyId());
        purchaseOrder.setManagerId(resolveManagerId(request));
        purchaseOrder.setDeliveryDate(request.deliveryDate());
        purchaseOrder.setIncotermsCode(request.incotermsCode());
        purchaseOrder.setNamedPlace(request.namedPlace());
        purchaseOrder.setSourceDeliveryDate(request.sourceDeliveryDate());
        purchaseOrder.setDeliveryDateOverride(Boolean.TRUE.equals(request.deliveryDateOverride()));
        purchaseOrder.setTotalAmount(totalAmount);
        purchaseOrder.setClientName(request.clientName());
        purchaseOrder.setClientAddress(request.clientAddress());
        purchaseOrder.setCountry(request.country());
        purchaseOrder.setCurrencyCode(request.currencyCode());
        purchaseOrder.setManagerName(request.managerName());
        purchaseOrder.setRemarks(request.remarks());
        // Issue C — PI → PO 로 buyerName 승계. 프론트가 linkedPi.buyerName 을 payload 로 전달.
        purchaseOrder.setBuyerName(request.buyerName());
        // Step C — 초안 수정에서도 분기/담당자 갱신 가능.
        String draftRoute = request.productionRoute();
        if (draftRoute != null && !draftRoute.isBlank()) {
            purchaseOrder.setProductionRoute(draftRoute.trim().toUpperCase());
        }
        if (request.productionAssigneeId() != null) {
            purchaseOrder.setProductionAssigneeId(request.productionAssigneeId());
        }
        if (request.shippingAssigneeId() != null) {
            purchaseOrder.setShippingAssigneeId(request.shippingAssigneeId());
        }
        purchaseOrder.setItemsSnapshot(serializeItemsSnapshot(newItems));

        purchaseOrder.getItems().clear();
        purchaseOrder.getItems().addAll(newItems);

        PurchaseOrder saved = purchaseOrderCommandService.save(purchaseOrder);
        docsSnapshotService.savePurchaseOrderSnapshot(saved);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                saved.getPoId(),
                "DRAFT_UPDATE",
                request.userId(),
                saved.getStatus().name(),
                "초안 PO를 수정했습니다."
        );
        return saved;
    }

    public PurchaseOrder create(PurchaseOrderCreateRequest request) {
        validateLinkedProformaInvoice(request.piId());
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

        // 생성자 서명에 remarks 를 추가하지 않고 @Setter 로 후설정.
        purchaseOrder.setRemarks(request.remarks());
        // Issue C — PI → PO 로 buyerName 승계. 프론트가 linkedPi.buyerName 을 payload 로 전달.
        purchaseOrder.setBuyerName(request.buyerName());

        // Step C — PO 등록 시점에 선택한 후속 흐름 분기 및 담당자 저장.
        // productionRoute: PRODUCTION(생산 경유) | DIRECT(직출하). null → DIRECT 로 해석.
        String route = request.productionRoute();
        if (route != null && !route.isBlank()) {
            purchaseOrder.setProductionRoute(route.trim().toUpperCase());
        } else {
            purchaseOrder.setProductionRoute("DIRECT");
        }
        purchaseOrder.setProductionAssigneeId(request.productionAssigneeId());
        purchaseOrder.setShippingAssigneeId(request.shippingAssigneeId());

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
                purchaseOrder.getClientId(),
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

    /**
     * PO 생성 시 연결된 PI 는 반드시 결재 확정(CONFIRMED) 상태여야 한다.
     * draft / pending_approval / modification_requested 등의 PI 로는 PO 를 만들 수 없다.
     */
    private void validateLinkedProformaInvoice(String piCode) {
        if (piCode == null || piCode.isBlank()) {
            throw new IllegalArgumentException("PO 는 연결할 PI 를 지정해야 합니다.");
        }
        ProformaInvoice linkedPi = proformaInvoiceRepository.findByPiCode(piCode)
                .orElseThrow(() -> new IllegalArgumentException("연결할 PI 를 찾을 수 없습니다: " + piCode));
        if (!ProformaInvoiceStatus.CONFIRMED.equals(linkedPi.getStatus())) {
            throw new IllegalStateException("결재 확정된 PI 만 PO 에 연결할 수 있습니다. (PI: " + piCode + ")");
        }
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
                        item.remark(),
                        // Issue D — master.items.item_weight(kg) 스냅샷. PL 자동생성 시
                        // SUM(qty × weight) 로 pl_gross_weight 계산 (createFromPurchaseOrder SQL).
                        item.itemWeight()
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
                .<Map<String, Object>>map(item -> {
                    java.util.LinkedHashMap<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("itemId", item.getItemId() == null ? 0 : item.getItemId());
                    row.put("itemName", item.getItemName());
                    row.put("quantity", item.getQuantity());
                    row.put("unit", item.getUnit() == null ? "" : item.getUnit());
                    row.put("unitPrice", item.getUnitPrice());
                    row.put("amount", item.getAmount());
                    row.put("remark", item.getRemark() == null ? "" : item.getRemark());
                    // Issue D — PL 과 Shipment 측 스냅샷 표시에도 kg 값 노출 가능하게.
                    row.put("itemWeight", item.getItemWeight() == null ? 0 : item.getItemWeight());
                    return row;
                })
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
