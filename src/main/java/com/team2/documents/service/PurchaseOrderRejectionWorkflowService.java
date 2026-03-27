package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalDocumentType;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderRejectionWorkflowService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public PurchaseOrderRejectionWorkflowService(PurchaseOrderRepository purchaseOrderRepository,
                                                 ApprovalRequestRepository approvalRequestRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    public void reject(String poId) {
        purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."))
                .reject();

        approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PO, poId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."))
                .reject();
    }
}
