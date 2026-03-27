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
    @DisplayName("팀장이 PO 등록 요청을 하면 즉시 확정된다")
    void confirmRegistration_whenDraft_thenChangesStatusToConfirmed() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);

        // when
        purchaseOrder.confirmRegistration();

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PO는 등록 요청할 수 없다")
    void requestRegistration_whenConfirmed_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, purchaseOrder::requestRegistration);
    }

    @Test
    @DisplayName("확정 상태의 PO는 수정 요청 시 결재대기 상태가 된다")
    void requestModification_whenConfirmed_thenChangesStatusToApprovalPending() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // when
        purchaseOrder.requestModification();

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("결재대기 상태의 PO는 수정 요청할 수 없다")
    void requestModification_whenApprovalPending_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.APPROVAL_PENDING);

        // when & then
        assertThrows(IllegalStateException.class, purchaseOrder::requestModification);
    }

    @Test
    @DisplayName("확정 상태의 PO는 삭제 요청 시 결재대기 상태가 된다")
    void requestDeletion_whenConfirmed_thenChangesStatusToApprovalPending() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // when
        purchaseOrder.requestDeletion();

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("결재대기 상태의 PO는 삭제 요청할 수 없다")
    void requestDeletion_whenApprovalPending_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.APPROVAL_PENDING);

        // when & then
        assertThrows(IllegalStateException.class, purchaseOrder::requestDeletion);
    }

    @Test
    @DisplayName("결재대기 상태의 PO는 승인 시 확정된다")
    void approve_whenApprovalPending_thenChangesStatusToConfirmed() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.APPROVAL_PENDING);

        // when
        purchaseOrder.approve();

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PO는 승인할 수 없다")
    void approve_whenConfirmed_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, purchaseOrder::approve);
    }

    @Test
    @DisplayName("결재대기 상태의 PO는 반려 시 반려 상태가 된다")
    void reject_whenApprovalPending_thenChangesStatusToRejected() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.APPROVAL_PENDING);

        // when
        purchaseOrder.reject();

        // then
        assertEquals(PurchaseOrderStatus.REJECTED, purchaseOrder.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PO는 반려할 수 없다")
    void reject_whenConfirmed_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, purchaseOrder::reject);
    }
}
