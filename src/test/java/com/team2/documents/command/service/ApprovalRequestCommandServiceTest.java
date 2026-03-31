package com.team2.documents.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.dto.ApprovalRequestCreateRequest;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.ApprovalStatus;
import com.team2.documents.command.repository.ApprovalRequestRepository;
import com.team2.documents.command.service.PurchaseOrderApprovalWorkflowService;
import com.team2.documents.command.service.PurchaseOrderRejectionWorkflowService;
import com.team2.documents.command.service.ProformaInvoiceApprovalWorkflowService;
import com.team2.documents.command.service.ProformaInvoiceRejectionWorkflowService;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestCommandServiceTest {

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @Mock
    private PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;

    @Mock
    private PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;

    @Mock
    private ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService;

    @Mock
    private ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    @InjectMocks
    private ApprovalRequestCommandService approvalRequestCommandService;

    @Test
    @DisplayName("결재 요청 생성 시 저장된 결재 요청을 반환한다")
    void create_whenRequestIsValid_thenReturnsSavedApprovalRequest() {
        // given
        ApprovalRequestCreateRequest request = new ApprovalRequestCreateRequest(
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다."
        );
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestRepository.save(org.mockito.ArgumentMatchers.any(ApprovalRequest.class)))
                .thenReturn(approvalRequest);

        // when
        ApprovalRequest saved = approvalRequestCommandService.create(request);

        // then
        assertEquals(1L, saved.getApprovalRequestId());
        assertEquals(ApprovalStatus.PENDING, saved.getStatus());
    }

    @Test
    @DisplayName("PO 결재 요청 승인 시 PO 승인 워크플로우를 호출한다")
    void update_whenPoApprovalRequestIsApproved_thenCallsPurchaseOrderApprovalWorkflow() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(approvalRequest));
        doNothing().when(purchaseOrderApprovalWorkflowService).approve("PO2025-0001");

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.APPROVED);

        // then
        assertEquals("PO2025-0001", result.getDocumentId());
        verify(purchaseOrderApprovalWorkflowService).approve("PO2025-0001");
    }

    @Test
    @DisplayName("PI 결재 요청 반려 시 PI 반려 워크플로우를 호출한다")
    void update_whenPiApprovalRequestIsRejected_thenCallsProformaInvoiceRejectionWorkflow() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PI,
                "PI2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(approvalRequest));
        doNothing().when(proformaInvoiceRejectionWorkflowService).reject("PI2025-0001");

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.REJECTED);

        // then
        assertEquals("PI2025-0001", result.getDocumentId());
        verify(proformaInvoiceRejectionWorkflowService).reject("PI2025-0001");
    }

    @Test
    @DisplayName("PI 결재 요청 승인 시 PI 승인 워크플로우를 호출한다")
    void update_whenPiApprovalRequestIsApproved_thenCallsProformaInvoiceApprovalWorkflow() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PI,
                "PI2025-0002",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(approvalRequest));
        doNothing().when(proformaInvoiceApprovalWorkflowService).approve("PI2025-0002");

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.APPROVED);

        // then
        assertEquals("PI2025-0002", result.getDocumentId());
        verify(proformaInvoiceApprovalWorkflowService).approve("PI2025-0002");
    }

    @Test
    @DisplayName("PO 결재 요청 반려 시 PO 반려 워크플로우를 호출한다")
    void update_whenPoApprovalRequestIsRejected_thenCallsPurchaseOrderRejectionWorkflow() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PO,
                "PO2025-0002",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(approvalRequest));
        doNothing().when(purchaseOrderRejectionWorkflowService).reject("PO2025-0002");

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.REJECTED);

        // then
        assertEquals("PO2025-0002", result.getDocumentId());
        verify(purchaseOrderRejectionWorkflowService).reject("PO2025-0002");
    }

    @Test
    @DisplayName("결재 요청 정보가 없으면 처리 시 예외가 발생한다")
    void update_whenApprovalRequestDoesNotExist_thenThrowsException() {
        // given
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> approvalRequestCommandService.update(1L, ApprovalStatus.APPROVED));
    }

    @Test
    @DisplayName("승인 또는 반려가 아닌 상태값으로 결재 요청을 처리하면 예외가 발생한다")
    void update_whenStatusIsNotApprovedOrRejected_thenThrowsException() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PO,
                "PO2025-0003",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(approvalRequest));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> approvalRequestCommandService.update(1L, ApprovalStatus.PENDING));
    }
}
