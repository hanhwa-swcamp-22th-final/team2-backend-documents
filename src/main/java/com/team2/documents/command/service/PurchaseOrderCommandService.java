package com.team2.documents.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.repository.PurchaseOrderRepository;

@Service
@Transactional
public class PurchaseOrderCommandService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderCommandService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public PurchaseOrder findById(String poId) {
        return purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));
    }

    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder updateStatus(String poId, PurchaseOrderStatus status) {
        PurchaseOrder purchaseOrder = findById(poId);
        purchaseOrder.setStatus(status);
        return purchaseOrderRepository.save(purchaseOrder);
    }
}
