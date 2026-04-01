package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class PurchaseOrderApprovalWorkflowService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    public PurchaseOrderApprovalWorkflowService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
    }

    public void approve(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 승인할 수 있습니다.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderCommandService.save(purchaseOrder);

        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
    }
}
