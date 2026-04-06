package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.repository.ApprovalRequestRepository;

@Service
@Transactional
public class ApprovalRequestCommandService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalDocumentMetadataService approvalDocumentMetadataService;
    private final ApprovalRequestDocumentWorkflowService approvalRequestDocumentWorkflowService;
    private final ApprovalRequestRevisionService approvalRequestRevisionService;

    public ApprovalRequestCommandService(ApprovalRequestRepository approvalRequestRepository,
                                         ApprovalDocumentMetadataService approvalDocumentMetadataService,
                                         ApprovalRequestDocumentWorkflowService approvalRequestDocumentWorkflowService,
                                         ApprovalRequestRevisionService approvalRequestRevisionService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.approvalDocumentMetadataService = approvalDocumentMetadataService;
        this.approvalRequestDocumentWorkflowService = approvalRequestDocumentWorkflowService;
        this.approvalRequestRevisionService = approvalRequestRevisionService;
    }

    public ApprovalRequest findById(Long approvalRequestId) {
        return approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다."));
    }

    public ApprovalRequest findPendingByDocument(ApprovalDocumentType documentType, String documentId) {
        return approvalRequestRepository.findPendingByDocument(documentType, documentId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."));
    }

    public ApprovalRequest save(ApprovalRequest approvalRequest) {
        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);
        java.util.Map<String, Object> beforeSnapshot = approvalRequestRevisionService.captureBeforeSnapshot(saved);
        approvalDocumentMetadataService.markRequested(saved);
        approvalRequestRevisionService.recordRequestEvent(saved, beforeSnapshot);
        return saved;
    }

    public ApprovalRequest create(ApprovalRequestCreateRequest request) {
        ApprovalRequest approvalRequest = new ApprovalRequest(
                request.documentType(),
                request.documentId(),
                request.requestType(),
                request.requesterId(),
                request.approverId(),
                request.comment()
        );
        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);
        java.util.Map<String, Object> beforeSnapshot = approvalRequestRevisionService.captureBeforeSnapshot(saved);
        approvalDocumentMetadataService.markRequested(saved);
        approvalRequestRevisionService.recordRequestEvent(saved, beforeSnapshot);
        return saved;
    }

    public ApprovalRequest update(Long approvalRequestId, ApprovalStatus targetApprovalStatus) {
        return update(approvalRequestId, targetApprovalStatus, null);
    }

    public ApprovalRequest updatePendingDocument(ApprovalDocumentType documentType,
                                                 String documentId,
                                                 ApprovalStatus targetApprovalStatus) {
        return updatePendingDocument(documentType, documentId, targetApprovalStatus, null);
    }

    public ApprovalRequest updatePendingDocument(ApprovalDocumentType documentType,
                                                 String documentId,
                                                 ApprovalStatus targetApprovalStatus,
                                                 String comment) {
        ApprovalRequest approvalRequest = findPendingByDocument(documentType, documentId);
        return update(approvalRequest.getApprovalRequestId(), targetApprovalStatus, comment);
    }

    public ApprovalRequest update(Long approvalRequestId, ApprovalStatus targetApprovalStatus, String comment) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다."));

        if (ApprovalStatus.APPROVED.equals(targetApprovalStatus)) {
            java.util.Map<String, Object> beforeSnapshot = approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest);
            approvalRequestDocumentWorkflowService.approveDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
            approvalRequest.setStatus(ApprovalStatus.APPROVED);
            approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
            approvalRequest.setReviewSnapshot(comment);
            approvalRequestRepository.save(approvalRequest);
            approvalDocumentMetadataService.markReviewed(approvalRequest, targetApprovalStatus, comment);
            approvalRequestRevisionService.recordReviewEvent(
                    approvalRequest, "REVIEW_APPROVED", targetApprovalStatus, comment, beforeSnapshot);
            return approvalRequest;
        }

        if (ApprovalStatus.REJECTED.equals(targetApprovalStatus)) {
            java.util.Map<String, Object> beforeSnapshot = approvalRequestRevisionService.captureBeforeSnapshot(approvalRequest);
            approvalRequestDocumentWorkflowService.rejectDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
            approvalRequest.setStatus(ApprovalStatus.REJECTED);
            approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
            approvalRequest.setReviewSnapshot(comment);
            approvalRequestRepository.save(approvalRequest);
            approvalDocumentMetadataService.markReviewed(approvalRequest, targetApprovalStatus, comment);
            approvalRequestRevisionService.recordReviewEvent(
                    approvalRequest, "REVIEW_REJECTED", targetApprovalStatus, comment, beforeSnapshot);
            return approvalRequest;
        }

        throw new IllegalArgumentException("승인 또는 반려 상태만 처리할 수 있습니다.");
    }
}
