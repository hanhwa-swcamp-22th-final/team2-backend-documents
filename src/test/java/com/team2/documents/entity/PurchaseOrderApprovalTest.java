package com.team2.documents.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PurchaseOrderApprovalTest {

    @Test
    @DisplayName("팀장이 생성한 PO는 즉시 확정된다")
    void determineInitialStatus_whenManagerCreatesPurchaseOrder_thenConfirmed() {
        // given
        PositionLevel positionLevel = PositionLevel.MANAGER;

        // when
        PurchaseOrderStatus status = PurchaseOrder.determineInitialStatus(positionLevel);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, status);
    }

    @Test
    @DisplayName("일반 직원이 생성한 PO는 결재대기 상태가 된다")
    void determineInitialStatus_whenStaffCreatesPurchaseOrder_thenApprovalPending() {
        // given
        PositionLevel positionLevel = PositionLevel.STAFF;

        // when
        PurchaseOrderStatus status = PurchaseOrder.determineInitialStatus(positionLevel);

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, status);
    }

    @Test
    @DisplayName("일반 직원이 PO 등록 요청을 하면 상태는 결재대기가 된다")
    void requestRegistration_whenDraft_thenChangesStatusToApprovalPending() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);

        // when
        purchaseOrder.requestRegistration();

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PO는 등록 요청할 수 없다")
    void requestRegistration_whenConfirmed_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, purchaseOrder::requestRegistration);
    }
}
