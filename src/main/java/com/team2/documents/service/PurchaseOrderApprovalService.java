package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderApprovalService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    public PurchaseOrderApprovalService(PurchaseOrderRepository purchaseOrderRepository,
                                        PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
    }

    public void approve(String poId) {
        com.team2.documents.entity.PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));
        if (!com.team2.documents.entity.enums.PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 승인할 수 있습니다.");
        }
        purchaseOrder.setStatus(com.team2.documents.entity.enums.PurchaseOrderStatus.CONFIRMED);

        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
    }
}
