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
        purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."))
                .approve();

        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
    }
}
