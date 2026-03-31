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
import com.team2.documents.command.service.PurchaseOrderCommandService;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderApprovalServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    @InjectMocks
    private PurchaseOrderApprovalService purchaseOrderApprovalService;

    @Test
    @DisplayName("결재대기 상태의 PO를 승인하면 상태가 확정되고 자동 생성 서비스가 호출된다")
    void approve_whenPurchaseOrderIsApprovalPending_thenConfirmsAndGeneratesDocuments() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);

        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when
        purchaseOrderApprovalService.approve(poId);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getStatus());
        verify(purchaseOrderDocumentGenerationService).generateOnConfirmation(poId);
    }

    @Test
    @DisplayName("PO 정보가 없으면 승인 시 예외가 발생한다")
    void approve_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-9999";
        when(purchaseOrderCommandService.findById(poId))
                .thenThrow(new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderApprovalService.approve(poId));
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PO를 승인하면 예외가 발생한다")
    void approve_whenPurchaseOrderIsNotApprovalPending_thenThrowsException() {
        // given
        String poId = "PO2025-0002";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);
        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderApprovalService.approve(poId));
    }
}
