package com.team2.documents.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApprovalRequestTest {

    @Test
    @DisplayName("결재 요청 생성 시 상태는 대기다")
    void create_whenApprovalRequestIsCreated_thenStatusIsPending() {
        // given
        ApprovalDocumentType documentType = ApprovalDocumentType.PO;
        String documentId = "PO2025-0001";
        ApprovalRequestType requestType = ApprovalRequestType.REGISTRATION;
        Long requesterId = 2L;
        Long approverId = 1L;

        // when
        ApprovalRequest approvalRequest = new ApprovalRequest(
                documentType,
                documentId,
                requestType,
                requesterId,
                approverId
        );

        // then
        assertEquals(ApprovalStatus.PENDING, approvalRequest.getStatus());
    }

    @Test
    @DisplayName("결재 요청 생성 시 요청 시각과 검토 스냅샷을 가진다")
    void create_whenApprovalRequestIsCreated_thenHasRequestedAtAndReviewSnapshot() {
        // given
        String reviewSnapshot = "{\"title\":\"PO 등록 결재 검토\"}";

        // when
        ApprovalRequest approvalRequest = new ApprovalRequest(
                null,
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                null,
                reviewSnapshot
        );

        // then
        assertEquals(reviewSnapshot, approvalRequest.getReviewSnapshot());
        org.junit.jupiter.api.Assertions.assertNotNull(approvalRequest.getRequestedAt());
    }

    @Test
    @DisplayName("대기 상태의 결재 요청은 승인할 수 있다")
    void approve_whenApprovalRequestIsPending_thenStatusIsApproved() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L
        );

        // when
        approvalRequest.approve();

        // then
        assertEquals(ApprovalStatus.APPROVED, approvalRequest.getStatus());
        org.junit.jupiter.api.Assertions.assertNotNull(approvalRequest.getReviewedAt());
    }

    @Test
    @DisplayName("대기 상태의 결재 요청은 반려할 수 있다")
    void reject_whenApprovalRequestIsPending_thenStatusIsRejected() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PI,
                "PI2025-0001",
                ApprovalRequestType.MODIFICATION,
                2L,
                1L
        );

        // when
        approvalRequest.reject();

        // then
        assertEquals(ApprovalStatus.REJECTED, approvalRequest.getStatus());
        org.junit.jupiter.api.Assertions.assertNotNull(approvalRequest.getReviewedAt());
    }

    @Test
    @DisplayName("승인된 결재 요청은 다시 승인할 수 없다")
    void approve_whenApprovalRequestIsAlreadyApproved_thenThrowsException() {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L
        );
        approvalRequest.approve();

        // when & then
        assertThrows(IllegalStateException.class, approvalRequest::approve);
    }
}
