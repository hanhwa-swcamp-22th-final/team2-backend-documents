package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@ExtendWith(MockitoExtension.class)
class ApprovalDocumentMetadataServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private UserSnapshotService userSnapshotService;

    @InjectMocks
    private ApprovalDocumentMetadataService approvalDocumentMetadataService;

    @Test
    @DisplayName("PO 결재 요청 시 승인 메타데이터를 대기 상태로 기록한다")
    void markRequested_whenPurchaseOrder_thenUpdatesApprovalMetadata() {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.DRAFT);
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO260001",
                ApprovalRequestType.MODIFICATION,
                2L,
                1L,
                "수정 결재"
        );
        approvalRequest.setRequestedAt(LocalDateTime.of(2026, 4, 7, 10, 0));

        when(purchaseOrderCommandService.findById("PO260001")).thenReturn(purchaseOrder);
        when(userSnapshotService.resolveRequesterDisplayName(2L)).thenReturn("이영업");

        approvalDocumentMetadataService.markRequested(approvalRequest);

        assertEquals("대기", purchaseOrder.getApprovalStatus());
        assertEquals("수정요청", purchaseOrder.getRequestStatus());
        assertEquals("수정", purchaseOrder.getApprovalAction());
        assertEquals("이영업", purchaseOrder.getApprovalRequestedBy());
        verify(purchaseOrderCommandService).save(purchaseOrder);
    }

    @Test
    @DisplayName("PO 결재 검토 시 승인 상태와 리뷰를 기록한다")
    void markReviewed_whenPurchaseOrder_thenUpdatesReviewMetadata() {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.APPROVAL_PENDING);
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO260001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "등록 결재"
        );

        when(purchaseOrderCommandService.findById("PO260001")).thenReturn(purchaseOrder);

        approvalDocumentMetadataService.markReviewed(approvalRequest, ApprovalStatus.APPROVED, "확인 완료");

        assertEquals("승인", purchaseOrder.getApprovalStatus());
        assertEquals("확인 완료", purchaseOrder.getApprovalReview());
        verify(purchaseOrderCommandService).save(purchaseOrder);
    }
}
