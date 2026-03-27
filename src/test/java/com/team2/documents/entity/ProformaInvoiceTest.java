package com.team2.documents.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProformaInvoiceTest {

    @Test
    @DisplayName("일반 직원이 PI 등록 요청을 하면 상태는 결재대기가 된다")
    void requestRegistration_whenDraft_thenChangesStatusToApprovalPending() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT);

        // when
        proformaInvoice.requestRegistration();

        // then
        assertEquals(ProformaInvoiceStatus.APPROVAL_PENDING, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("팀장이 PI 등록 요청을 하면 즉시 확정된다")
    void confirmRegistration_whenDraft_thenChangesStatusToConfirmed() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT);

        // when
        proformaInvoice.confirmRegistration();

        // then
        assertEquals(ProformaInvoiceStatus.CONFIRMED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PI는 등록 요청할 수 없다")
    void requestRegistration_whenConfirmed_thenThrowsException() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, proformaInvoice::requestRegistration);
    }

    @Test
    @DisplayName("결재대기 상태의 PI는 승인 시 확정된다")
    void approve_whenApprovalPending_thenChangesStatusToConfirmed() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.APPROVAL_PENDING);

        // when
        proformaInvoice.approve();

        // then
        assertEquals(ProformaInvoiceStatus.CONFIRMED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PI는 승인할 수 없다")
    void approve_whenConfirmed_thenThrowsException() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, proformaInvoice::approve);
    }

    @Test
    @DisplayName("결재대기 상태의 PI는 반려 시 반려 상태가 된다")
    void reject_whenApprovalPending_thenChangesStatusToRejected() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.APPROVAL_PENDING);

        // when
        proformaInvoice.reject();

        // then
        assertEquals(ProformaInvoiceStatus.REJECTED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("확정 상태의 PI는 반려할 수 없다")
    void reject_whenConfirmed_thenThrowsException() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, proformaInvoice::reject);
    }
}
