package com.team2.documents.command.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

@DataJpaTest
class ApprovalRequestRepositoryTest {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Test
    @DisplayName("결재 요청 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenApprovalRequestExists_thenReturnsEntity() {
        ApprovalRequest saved = approvalRequestRepository.save(new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청"
        ));

        ApprovalRequest result = approvalRequestRepository.findById(saved.getApprovalRequestId()).orElseThrow();

        assertEquals(ApprovalDocumentType.PO, result.getDocumentType());
        assertEquals(ApprovalStatus.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("대기 중인 결재 요청을 문서 기준으로 조회할 수 있다")
    void findPendingByDocument_whenPendingApprovalRequestExists_thenReturnsEntity() {
        approvalRequestRepository.save(new ApprovalRequest(
                ApprovalDocumentType.PI,
                "PI2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청"
        ));

        ApprovalRequest result = approvalRequestRepository
                .findPendingByDocument(ApprovalDocumentType.PI, "PI2025-0001")
                .orElseThrow();

        assertEquals(ApprovalStatus.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("결재 요청 엔티티를 수정할 수 있다")
    void update_whenApprovalRequestStatusChanges_thenPersistsUpdatedStatus() {
        ApprovalRequest approvalRequest = approvalRequestRepository.save(new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO2025-0002",
                ApprovalRequestType.MODIFICATION,
                2L,
                1L,
                "수정 결재"
        ));

        approvalRequest.setStatus(ApprovalStatus.APPROVED);
        approvalRequest.setReviewedAt(LocalDateTime.of(2026, 3, 30, 10, 0));
        approvalRequestRepository.save(approvalRequest);

        ApprovalRequest result = approvalRequestRepository.findById(approvalRequest.getApprovalRequestId()).orElseThrow();
        assertEquals(ApprovalStatus.APPROVED, result.getStatus());
        assertEquals(LocalDateTime.of(2026, 3, 30, 10, 0), result.getReviewedAt());
    }

    @Test
    @DisplayName("결재 요청 엔티티를 삭제할 수 있다")
    void delete_whenApprovalRequestExists_thenRemovesEntity() {
        ApprovalRequest approvalRequest = approvalRequestRepository.save(new ApprovalRequest(
                ApprovalDocumentType.PO,
                "PO2025-0003",
                ApprovalRequestType.DELETION,
                2L,
                1L,
                "삭제 결재"
        ));

        approvalRequestRepository.delete(approvalRequest);

        assertFalse(approvalRequestRepository.findById(approvalRequest.getApprovalRequestId()).isPresent());
    }

    @Test
    @DisplayName("결재 요청 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenApprovalRequestsExist_thenReturnsAllEntities() {
        approvalRequestRepository.save(new ApprovalRequest(
                ApprovalDocumentType.PO, "PO2025-0100", ApprovalRequestType.REGISTRATION, 2L, 1L, null));
        approvalRequestRepository.save(new ApprovalRequest(
                ApprovalDocumentType.PI, "PI2025-0100", ApprovalRequestType.REGISTRATION, 2L, 1L, null));

        assertTrue(approvalRequestRepository.findAll().size() >= 2);
    }
}
