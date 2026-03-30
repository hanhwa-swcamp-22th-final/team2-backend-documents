package com.team2.documents.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalStatus;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
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
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));
        if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 반려할 수 있습니다.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.REJECTED);

        ApprovalRequest approvalRequest = approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PO, poId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."));
        approvalRequest.setStatus(ApprovalStatus.REJECTED);
        approvalRequest.setReviewedAt(LocalDateTime.now());
    }
}
