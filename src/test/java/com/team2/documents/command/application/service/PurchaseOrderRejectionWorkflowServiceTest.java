package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderRejectionWorkflowServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private DocumentRevisionHistoryService documentRevisionHistoryService;

    @InjectMocks
    private PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;

    @Test
    @DisplayName("PO 반려 시 PO 상태가 REJECTED로 변경된다")
    void reject_whenApprovalPending_thenRejectsPurchaseOrder() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);

        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when
        purchaseOrderRejectionWorkflowService.reject(poId);

        // then
        assertEquals(PurchaseOrderStatus.REJECTED, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PO를 워크플로우 반려하면 예외가 발생한다")
    void reject_whenPurchaseOrderIsNotApprovalPending_thenThrowsException() {
        // given
        String poId = "PO2025-0002";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);
        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderRejectionWorkflowService.reject(poId));
    }
}
