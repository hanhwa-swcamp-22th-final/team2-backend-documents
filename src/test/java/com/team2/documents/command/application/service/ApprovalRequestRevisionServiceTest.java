package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestRevisionServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private DocumentRevisionHistoryService documentRevisionHistoryService;

    @InjectMocks
    private ApprovalRequestRevisionService approvalRequestRevisionService;

    @Test
    @DisplayName("PO 요청 전 스냅샷은 PO revision history에서 캡처한다")
    void captureBeforeSnapshot_whenDocumentTypeIsPo_thenReturnsPurchaseOrderSnapshot() {
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO260001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "등록 결재"
        );
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.DRAFT);
        Map<String, Object> snapshot = Map.of("status", "DRAFT");

        when(purchaseOrderCommandService.findById("PO260001")).thenReturn(purchaseOrder);
        when(documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder)).thenReturn(snapshot);

        Map<String, Object> result = approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest);

        assertEquals(snapshot, result);
    }

    @Test
    @DisplayName("PI 요청 이벤트는 APPROVAL_PENDING 상태로 revision history에 기록한다")
    void recordRequestEvent_whenDocumentTypeIsPi_thenDelegatesToProformaInvoiceHistory() {
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PI,
                "PI260001",
                ApprovalRequestType.MODIFICATION,
                2L,
                1L,
                "수정 결재"
        );
        approvalRequestRevisionService.recordRequestEvent(approvalRequest, Map.of("status", "DRAFT"));

        verify(documentRevisionHistoryService).recordProformaInvoiceEvent(
                "PI260001",
                "REQUEST_MODIFICATION",
                2L,
                "APPROVAL_PENDING",
                "수정 결재를 요청했습니다.",
                Map.of("status", "DRAFT")
        );
    }
}
