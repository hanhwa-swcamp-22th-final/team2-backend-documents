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

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.application.service.ProformaInvoiceCommandService;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceRejectionServiceTest {

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @InjectMocks
    private ProformaInvoiceRejectionService proformaInvoiceRejectionService;

    @Test
    @DisplayName("결재대기 상태의 PI를 반려하면 상태가 반려로 바뀐다")
    void reject_whenProformaInvoiceIsApprovalPending_thenChangesStatusToRejected() {
        // given
        String piId = "PI2025-0001";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.APPROVAL_PENDING);

        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);

        // when
        proformaInvoiceRejectionService.reject(piId);

        // then
        assertEquals(ProformaInvoiceStatus.REJECTED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("PI 정보가 없으면 반려 시 예외가 발생한다")
    void reject_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        String piId = "PI2025-9999";
        when(proformaInvoiceCommandService.findById(piId))
                .thenThrow(new IllegalArgumentException("PI 정보를 찾을 수 없습니다."));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceRejectionService.reject(piId));
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PI를 반려하면 예외가 발생한다")
    void reject_whenProformaInvoiceIsNotApprovalPending_thenThrowsException() {
        // given
        String piId = "PI2025-0002";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.CONFIRMED);
        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> proformaInvoiceRejectionService.reject(piId));
    }
}
