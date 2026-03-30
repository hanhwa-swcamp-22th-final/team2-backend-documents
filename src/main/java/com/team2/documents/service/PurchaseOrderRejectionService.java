package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderRejectionService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderRejectionService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public void reject(String poId) {
        com.team2.documents.entity.PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));
        if (!com.team2.documents.entity.enums.PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 반려할 수 있습니다.");
        }
        purchaseOrder.setStatus(com.team2.documents.entity.enums.PurchaseOrderStatus.REJECTED);
    }
}
