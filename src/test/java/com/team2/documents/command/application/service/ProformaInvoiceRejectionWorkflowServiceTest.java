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

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceRejectionWorkflowServiceTest {

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private DocumentRevisionHistoryService documentRevisionHistoryService;

    @InjectMocks
    private ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    @Test
    @DisplayName("PI 반려 시 PI 상태가 REJECTED로 변경된다")
    void reject_whenProformaInvoiceIsApprovalPending_thenRejectsDocument() {
        // given
        String piId = "PI2025-0001";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.APPROVAL_PENDING);

        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);

        // when
        proformaInvoiceRejectionWorkflowService.reject(piId);

        // then
        assertEquals(ProformaInvoiceStatus.REJECTED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PI를 워크플로우 반려하면 예외가 발생한다")
    void reject_whenProformaInvoiceIsNotApprovalPending_thenThrowsException() {
        // given
        String piId = "PI2025-0002";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.CONFIRMED);
        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> proformaInvoiceRejectionWorkflowService.reject(piId));
    }
}
