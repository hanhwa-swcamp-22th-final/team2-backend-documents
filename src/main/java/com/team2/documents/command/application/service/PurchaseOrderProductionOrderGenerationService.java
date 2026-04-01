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

    public PurchaseOrderProductionOrderGenerationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                         ProductionOrderCommandService productionOrderCommandService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.productionOrderCommandService = productionOrderCommandService;
    }

    public void generate(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 선택 생성 문서를 가질 수 있습니다.");
        }

        productionOrderCommandService.save(new ProductionOrder(
                "TEMP-PRD-" + poId,
                poId,
                null,
                java.time.LocalDate.now(),
                null,
                "진행중",
                java.util.List.of(),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        ));
    }
}
