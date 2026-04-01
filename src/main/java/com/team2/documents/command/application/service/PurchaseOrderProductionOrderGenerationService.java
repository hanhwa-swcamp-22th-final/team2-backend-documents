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

    public PurchaseOrderProductionOrderGenerationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                         ProductionOrderCommandService productionOrderCommandService,
                                                         DocumentNumberGeneratorService documentNumberGeneratorService,
                                                         DocumentLinkService documentLinkService,
                                                         DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.productionOrderCommandService = productionOrderCommandService;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void generate(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 선택 생성 문서를 가질 수 있습니다.");
        }

        String productionOrderId = documentNumberGeneratorService.nextProductionOrderId();
        productionOrderCommandService.save(new ProductionOrder(
                productionOrderId,
                poId,
                java.time.LocalDate.now(),
                purchaseOrder.getClientId(),
                purchaseOrder.getManagerId(),
                purchaseOrder.getDeliveryDate(),
                "진행중",
                purchaseOrder.getClientName(),
                purchaseOrder.getCountry(),
                purchaseOrder.getManagerName(),
                purchaseOrder.getItems().isEmpty() ? null : purchaseOrder.getItems().get(0).getItemName(),
                "[{\"id\":\"" + poId + "\",\"type\":\"PO\",\"status\":\"" + purchaseOrder.getStatus().name() + "\"}]",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        ));
        documentLinkService.linkProductionOrder(poId, productionOrderId);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "GENERATE_PRODUCTION_ORDER",
                purchaseOrder.getManagerId(),
                purchaseOrder.getStatus().name(),
                "생산지시서를 선택 생성했습니다."
        );
    }
}
