package com.team2.documents.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderApprovalWorkflowServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    @InjectMocks
    private PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;

    @Test
    @DisplayName("PO 승인 시 PO 상태가 CONFIRMED로 변경된다")
    void approve_whenApprovalPending_thenConfirmsPurchaseOrder() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);

        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when
        purchaseOrderApprovalWorkflowService.approve(poId);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getStatus());
        verify(purchaseOrderDocumentGenerationService).generateOnConfirmation(poId);
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PO를 워크플로우 승인하면 예외가 발생한다")
    void approve_whenPurchaseOrderIsNotApprovalPending_thenThrowsException() {
        // given
        String poId = "PO2025-0002";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);
        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderApprovalWorkflowService.approve(poId));
    }
}
