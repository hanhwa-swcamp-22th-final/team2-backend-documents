package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class PurchaseOrderRejectionWorkflowService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public PurchaseOrderRejectionWorkflowService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                 DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void reject(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 반려할 수 있습니다.");
        }
        java.util.Map<String, Object> beforeSnapshot = documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);
        purchaseOrder.setStatus(PurchaseOrderStatus.REJECTED);
        purchaseOrderCommandService.save(purchaseOrder);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "REJECTED",
                purchaseOrder.getManagerId(),
                PurchaseOrderStatus.REJECTED.name(),
                "PO 등록 요청이 반려되었습니다.",
                beforeSnapshot
        );
    }
}
