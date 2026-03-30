package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.ApprovalStatus;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderRejectionWorkflowServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @InjectMocks
    private PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;

    @Test
    @DisplayName("PO 반려 시 approval request도 반려 상태가 된다")
    void reject_whenApprovalRequestExists_thenRejectsRequestAndPurchaseOrder() {
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
        purchaseOrderRejectionWorkflowService.reject(poId);

        // then
        assertEquals(PurchaseOrderStatus.REJECTED, purchaseOrder.getStatus());
        assertEquals(ApprovalStatus.REJECTED, approvalRequest.getStatus());
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PO를 워크플로우 반려하면 예외가 발생한다")
    void reject_whenPurchaseOrderIsNotApprovalPending_thenThrowsException() {
        // given
        String poId = "PO2025-0002";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderRejectionWorkflowService.reject(poId));
    }
}
