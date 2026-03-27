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

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.PurchaseOrderStatus;
import com.team2.documents.repository.PurchaseOrderRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderRejectionServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderRejectionService purchaseOrderRejectionService;

    @Test
    @DisplayName("결재대기 상태의 PO를 반려하면 상태가 반려로 바뀐다")
    void reject_whenPurchaseOrderIsApprovalPending_thenChangesStatusToRejected() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));

        // when
        purchaseOrderRejectionService.reject(poId);

        // then
        assertEquals(PurchaseOrderStatus.REJECTED, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("PO 정보가 없으면 반려 시 예외가 발생한다")
    void reject_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-9999";
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderRejectionService.reject(poId));
    }
}
