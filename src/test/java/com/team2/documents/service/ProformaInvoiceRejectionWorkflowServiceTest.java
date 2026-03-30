package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.ApprovalStatus;
import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.ProformaInvoiceRepository;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceRejectionWorkflowServiceTest {

    @Mock
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @InjectMocks
    private ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    @Test
    @DisplayName("PI 반려 시 approval request도 반려 상태가 된다")
    void reject_whenProformaInvoiceIsApprovalPending_thenRejectsDocumentAndApprovalRequest() {
        // given
        String piId = "PI2025-0001";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.APPROVAL_PENDING);
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PI,
                piId,
                ApprovalRequestType.REGISTRATION,
                2L,
                1L
        );

        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));
        when(approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PI, piId))
                .thenReturn(Optional.of(approvalRequest));

        // when
        proformaInvoiceRejectionWorkflowService.reject(piId);

        // then
        assertEquals(ProformaInvoiceStatus.REJECTED, proformaInvoice.getStatus());
        assertEquals(ApprovalStatus.REJECTED, approvalRequest.getStatus());
    }
}
