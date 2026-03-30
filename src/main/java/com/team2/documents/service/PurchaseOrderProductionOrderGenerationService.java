package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ProductionOrder;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ProductionOrderRepository;
import com.team2.documents.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderProductionOrderGenerationService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductionOrderRepository productionOrderRepository;

    public PurchaseOrderProductionOrderGenerationService(PurchaseOrderRepository purchaseOrderRepository,
                                                         ProductionOrderRepository productionOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productionOrderRepository = productionOrderRepository;
    }

    public void generate(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 선택 생성 문서를 가질 수 있습니다.");
        }

        productionOrderRepository.save(new ProductionOrder(
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
