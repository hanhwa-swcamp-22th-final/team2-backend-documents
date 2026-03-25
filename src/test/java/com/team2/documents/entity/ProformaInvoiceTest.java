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
    @DisplayName("확정 상태의 PI는 등록 요청할 수 없다")
    void requestRegistration_whenConfirmed_thenThrowsException() {
        // given
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.CONFIRMED);

        // when & then
        assertThrows(IllegalStateException.class, proformaInvoice::requestRegistration);
    }
}
