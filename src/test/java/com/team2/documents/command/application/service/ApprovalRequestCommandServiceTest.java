package com.team2.documents.command.application.service;

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

import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.ApprovalRequestRepository;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestCommandServiceTest {

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @Mock
    private ApprovalDocumentMetadataService approvalDocumentMetadataService;

    @Mock
    private ApprovalRequestDocumentWorkflowService approvalRequestDocumentWorkflowService;

    @Mock
    private ApprovalRequestRevisionService approvalRequestRevisionService;

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
        when(approvalRequestRevisionService.captureBeforeSnapshot(org.mockito.ArgumentMatchers.any(ApprovalRequest.class)))
                .thenReturn(java.util.Map.of("status", PurchaseOrderStatus.APPROVAL_PENDING.name()));

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
        doNothing().when(approvalRequestDocumentWorkflowService).approveDocument(ApprovalDocumentType.PO, "PO2025-0001");
        when(approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest))
                .thenReturn(java.util.Map.of("status", PurchaseOrderStatus.APPROVAL_PENDING.name()));

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.APPROVED);

        // then
        assertEquals("PO2025-0001", result.getDocumentId());
        verify(approvalRequestDocumentWorkflowService).approveDocument(ApprovalDocumentType.PO, "PO2025-0001");
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
        doNothing().when(approvalRequestDocumentWorkflowService).rejectDocument(ApprovalDocumentType.PI, "PI2025-0001");
        when(approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest))
                .thenReturn(java.util.Map.of("status", ProformaInvoiceStatus.APPROVAL_PENDING.name()));

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.REJECTED);

        // then
        assertEquals("PI2025-0001", result.getDocumentId());
        verify(approvalRequestDocumentWorkflowService).rejectDocument(ApprovalDocumentType.PI, "PI2025-0001");
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
        doNothing().when(approvalRequestDocumentWorkflowService).approveDocument(ApprovalDocumentType.PI, "PI2025-0002");
        when(approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest))
                .thenReturn(java.util.Map.of("status", ProformaInvoiceStatus.APPROVAL_PENDING.name()));

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.APPROVED);

        // then
        assertEquals("PI2025-0002", result.getDocumentId());
        verify(approvalRequestDocumentWorkflowService).approveDocument(ApprovalDocumentType.PI, "PI2025-0002");
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
        doNothing().when(approvalRequestDocumentWorkflowService).rejectDocument(ApprovalDocumentType.PO, "PO2025-0002");
        when(approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest))
                .thenReturn(java.util.Map.of("status", PurchaseOrderStatus.APPROVAL_PENDING.name()));

        // when
        ApprovalRequest result = approvalRequestCommandService.update(1L, ApprovalStatus.REJECTED);

        // then
        assertEquals("PO2025-0002", result.getDocumentId());
        verify(approvalRequestDocumentWorkflowService).rejectDocument(ApprovalDocumentType.PO, "PO2025-0002");
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
    @DisplayName("결재 요청 ID로 조회 시 해당 결재 요청을 반환한다")
    void findById_whenApprovalRequestExists_thenReturnsApprovalRequest() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L, ApprovalDocumentType.PO, "PO2025-0001",
                ApprovalRequestType.REGISTRATION, 2L, 1L, "결재 요청", null);
        when(approvalRequestRepository.findById(1L)).thenReturn(Optional.of(approvalRequest));

        // when
        ApprovalRequest result = approvalRequestCommandService.findById(1L);

        // then
        assertEquals(1L, result.getApprovalRequestId());
    }

    @Test
    @DisplayName("존재하지 않는 결재 요청 ID로 조회 시 예외가 발생한다")
    void findById_whenApprovalRequestDoesNotExist_thenThrowsException() {
        // given
        when(approvalRequestRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> approvalRequestCommandService.findById(999L));
    }

    @Test
    @DisplayName("대기 중인 결재 요청을 문서 유형과 ID로 조회한다")
    void findPendingByDocument_whenApprovalRequestExists_thenReturnsApprovalRequest() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L, ApprovalDocumentType.PO, "PO2025-0001",
                ApprovalRequestType.REGISTRATION, 2L, 1L, "결재 요청", null);
        when(approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PO, "PO2025-0001"))
                .thenReturn(Optional.of(approvalRequest));

        // when
        ApprovalRequest result = approvalRequestCommandService.findPendingByDocument(
                ApprovalDocumentType.PO, "PO2025-0001");

        // then
        assertEquals("PO2025-0001", result.getDocumentId());
    }

    @Test
    @DisplayName("대기 중인 결재 요청이 없으면 예외가 발생한다")
    void findPendingByDocument_whenApprovalRequestDoesNotExist_thenThrowsException() {
        // given
        when(approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PO, "NOT-EXIST"))
                .thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> approvalRequestCommandService.findPendingByDocument(ApprovalDocumentType.PO, "NOT-EXIST"));
    }

    @Test
    @DisplayName("결재 요청 저장 시 저장된 결재 요청을 반환한다")
    void save_whenApprovalRequestIsValid_thenReturnsSavedApprovalRequest() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L, ApprovalDocumentType.PO, "PO2025-0001",
                ApprovalRequestType.REGISTRATION, 2L, 1L, "결재 요청", null);
        when(approvalRequestRepository.save(org.mockito.ArgumentMatchers.any(ApprovalRequest.class)))
                .thenReturn(approvalRequest);
        when(approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest))
                .thenReturn(java.util.Map.of("status", PurchaseOrderStatus.APPROVAL_PENDING.name()));

        // when
        ApprovalRequest result = approvalRequestCommandService.save(approvalRequest);

        // then
        assertEquals(1L, result.getApprovalRequestId());
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
