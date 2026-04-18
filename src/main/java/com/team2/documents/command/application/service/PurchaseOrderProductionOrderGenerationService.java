package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class PurchaseOrderProductionOrderGenerationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProductionOrderCommandService productionOrderCommandService;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentLinkService documentLinkService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final DocumentAutoMailService documentAutoMailService;

    public PurchaseOrderProductionOrderGenerationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                         ProductionOrderCommandService productionOrderCommandService,
                                                         DocumentNumberGeneratorService documentNumberGeneratorService,
                                                         DocumentLinkService documentLinkService,
                                                         DocumentRevisionHistoryService documentRevisionHistoryService,
                                                         DocumentAutoMailService documentAutoMailService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.productionOrderCommandService = productionOrderCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.documentAutoMailService = documentAutoMailService;
    }

    public void generate(String poId) {
        generate(poId, null, null);
    }

    /**
     * 생산지시서 발행. assigneeUserId 가 주어지면 생산담당자로 할당(생산 role 유저 권장).
     * null 이면 PO 의 영업담당자(managerId) 를 그대로 승계 (기존 동작).
     */
    public void generate(String poId, Long assigneeUserId, String assigneeName) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 선택 생성 문서를 가질 수 있습니다.");
        }

        Long effectiveManagerId = assigneeUserId != null ? assigneeUserId : purchaseOrder.getManagerId();
        String effectiveManagerName = (assigneeName != null && !assigneeName.isBlank())
                ? assigneeName
                : purchaseOrder.getManagerName();

        String productionOrderId = documentNumberGeneratorService.nextProductionOrderId();
        ProductionOrder productionOrder = new ProductionOrder(
                productionOrderId,
                purchaseOrder.getPurchaseOrderId(),
                poId,
                java.time.LocalDate.now(),
                purchaseOrder.getClientId(),
                effectiveManagerId,
                purchaseOrder.getDeliveryDate(),
                "진행중",
                purchaseOrder.getClientName(),
                purchaseOrder.getCountry(),
                effectiveManagerName,
                purchaseOrder.getItems().isEmpty() ? null : purchaseOrder.getItems().get(0).getItemName(),
                "[{\"id\":\"" + poId + "\",\"type\":\"PO\",\"status\":\"" + purchaseOrder.getStatus().name() + "\"}]",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
        productionOrderCommandService.save(productionOrder);
        documentLinkService.linkProductionOrder(poId, productionOrderId);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "GENERATE_PRODUCTION_ORDER",
                purchaseOrder.getManagerId(),
                purchaseOrder.getStatus().name(),
                "생산지시서를 선택 생성했습니다."
                        + (assigneeUserId != null ? " (담당자 userId=" + assigneeUserId + ")" : "")
        );
        documentAutoMailService.sendProductionOrderToProductionTeam(purchaseOrder, productionOrder);
    }
}
