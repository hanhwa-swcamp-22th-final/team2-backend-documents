package com.team2.documents.command.application.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceRejectionWorkflowServiceTest {

    @Mock
    private ApprovalRequestCommandService approvalRequestCommandService;

    @InjectMocks
    private ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    @Test
    @DisplayName("PI 반려 시 대기 중인 결재 요청을 반려 처리한다")
    void reject_whenCalled_thenRejectsPendingApprovalRequest() {
        String piId = "PI2025-0001";
        when(approvalRequestCommandService.updatePendingDocument(ApprovalDocumentType.PI, piId, ApprovalStatus.REJECTED))
                .thenReturn(null);

        proformaInvoiceRejectionWorkflowService.reject(piId);

        verify(approvalRequestCommandService)
                .updatePendingDocument(ApprovalDocumentType.PI, piId, ApprovalStatus.REJECTED);
    }
}
