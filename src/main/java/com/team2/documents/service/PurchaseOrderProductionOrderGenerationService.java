package com.team2.documents.service;

import org.springframework.stereotype.Service;

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
        purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."))
                .getSelectableGeneratedDocuments();

        productionOrderRepository.createFromPurchaseOrder(poId);
    }
}
