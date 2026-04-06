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
class PurchaseOrderApprovalWorkflowServiceTest {

    @Mock
    private ApprovalRequestCommandService approvalRequestCommandService;

    @InjectMocks
    private PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;

    @Test
    @DisplayName("PO 승인 시 대기 중인 결재 요청을 승인 처리한다")
    void approve_whenCalled_thenApprovesPendingApprovalRequest() {
        String poId = "PO2025-0001";
        when(approvalRequestCommandService.updatePendingDocument(ApprovalDocumentType.PO, poId, ApprovalStatus.APPROVED))
                .thenReturn(null);

        purchaseOrderApprovalWorkflowService.approve(poId);

        verify(approvalRequestCommandService)
                .updatePendingDocument(ApprovalDocumentType.PO, poId, ApprovalStatus.APPROVED);
    }
}
