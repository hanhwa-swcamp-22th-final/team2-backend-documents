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
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public PurchaseOrderApprovalWorkflowService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService,
                                                DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void approve(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 승인할 수 있습니다.");
        }
        java.util.Map<String, Object> beforeSnapshot = documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);
        purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderCommandService.save(purchaseOrder);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "APPROVED",
                purchaseOrder.getManagerId(),
                PurchaseOrderStatus.CONFIRMED.name(),
                "PO 등록 요청이 승인되었습니다.",
                beforeSnapshot
        );

        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
    }
}
