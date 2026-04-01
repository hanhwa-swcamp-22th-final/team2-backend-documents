package com.team2.documents.command.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.repository.UserPositionRepository;
import com.team2.documents.dto.PurchaseOrderCreateRequest;
import com.team2.documents.dto.PurchaseOrderItemCreateRequest;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.PurchaseOrderItem;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class PurchaseOrderCreationService {

    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ObjectMapper objectMapper;

    public PurchaseOrderCreationService(UserPositionRepository userPositionRepository,
                                        ApprovalRequestCommandService approvalRequestCommandService,
                                        PurchaseOrderCommandService purchaseOrderCommandService,
                                        ObjectMapper objectMapper) {
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.objectMapper = objectMapper;
    }

    public PurchaseOrderStatus determineInitialStatus(Long userId) {
        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            return PurchaseOrderStatus.CONFIRMED;
        }
        return PurchaseOrderStatus.APPROVAL_PENDING;
    }

    public PurchaseOrder create(PurchaseOrderCreateRequest request) {
        PurchaseOrderStatus initialStatus = determineInitialStatus(request.userId());
        String poId = request.poId() == null || request.poId().isBlank()
                ? "TEMP-PO-" + UUID.randomUUID().toString().substring(0, 8)
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
                initialStatus == PurchaseOrderStatus.APPROVAL_PENDING ? "대기" : "승인",
                initialStatus == PurchaseOrderStatus.APPROVAL_PENDING ? "등록요청" : null,
                initialStatus == PurchaseOrderStatus.APPROVAL_PENDING ? "등록" : null,
                request.managerName(),
                initialStatus == PurchaseOrderStatus.APPROVAL_PENDING ? LocalDateTime.now() : null,
                null,
                serializeItemsSnapshot(items),
                serializeLinkedDocuments(),
                serializeRevisionHistory(request.userId(), initialStatus, createdAt),
                items
        );

        PurchaseOrder saved = purchaseOrderCommandService.save(purchaseOrder);
        if (PurchaseOrderStatus.APPROVAL_PENDING.equals(initialStatus)) {
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    saved.getPoId(),
                    ApprovalRequestType.REGISTRATION,
                    request.userId(),
                    1L
            ));
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
