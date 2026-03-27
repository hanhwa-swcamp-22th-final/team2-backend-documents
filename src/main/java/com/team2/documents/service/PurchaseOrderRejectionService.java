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
        purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."))
                .reject();
    }
}
