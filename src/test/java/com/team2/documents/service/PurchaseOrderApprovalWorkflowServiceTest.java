package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.ApprovalRequestType;
import com.team2.documents.entity.ApprovalStatus;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderApprovalWorkflowServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @Mock
    private PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    @InjectMocks
    private PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;

    @Test
    @DisplayName("PO 승인 시 approval request도 승인 상태가 된다")
    void approve_whenApprovalRequestExists_thenApprovesRequestAndPurchaseOrder() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PO,
                poId,
                ApprovalRequestType.REGISTRATION,
                2L,
                1L
        );

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));
        when(approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PO, poId))
                .thenReturn(Optional.of(approvalRequest));

        // when
        purchaseOrderApprovalWorkflowService.approve(poId);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getStatus());
        assertEquals(ApprovalStatus.APPROVED, approvalRequest.getStatus());
        verify(purchaseOrderDocumentGenerationService).generateOnConfirmation(poId);
    }
}
