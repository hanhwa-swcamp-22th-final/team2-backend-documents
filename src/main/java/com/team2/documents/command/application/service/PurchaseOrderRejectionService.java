package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class PurchaseOrderRejectionService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;

    public PurchaseOrderRejectionService(PurchaseOrderCommandService purchaseOrderCommandService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
    }

    public void reject(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 반려할 수 있습니다.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.REJECTED);
        purchaseOrderCommandService.save(purchaseOrder);
    }
}
