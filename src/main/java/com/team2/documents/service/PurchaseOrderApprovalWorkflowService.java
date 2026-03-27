package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalDocumentType;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderApprovalWorkflowService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    public PurchaseOrderApprovalWorkflowService(PurchaseOrderRepository purchaseOrderRepository,
                                                ApprovalRequestRepository approvalRequestRepository,
                                                PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
    }

    public void approve(String poId) {
        purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."))
                .approve();

        approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PO, poId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."))
                .approve();

        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
    }
}
