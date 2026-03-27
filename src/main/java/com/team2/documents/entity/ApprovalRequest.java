package com.team2.documents.entity;

import java.time.LocalDateTime;

public class ApprovalRequest {

    private final Long approvalRequestId;
    private final ApprovalDocumentType documentType;
    private final String documentId;
    private final ApprovalRequestType requestType;
    private final Long requesterId;
    private final Long approverId;
    private final String comment;
    private final String reviewSnapshot;
    private final LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private ApprovalStatus status;

    public ApprovalRequest(ApprovalDocumentType documentType,
                           String documentId,
                           ApprovalRequestType requestType,
                           Long requesterId,
                           Long approverId,
                           String comment) {
        this(null, documentType, documentId, requestType, requesterId, approverId, comment, null);
    }

    public ApprovalRequest(ApprovalDocumentType documentType,
                           String documentId,
                           ApprovalRequestType requestType,
                           Long requesterId,
                           Long approverId) {
        this(null, documentType, documentId, requestType, requesterId, approverId, null, null);
    }

    public ApprovalRequest(Long approvalRequestId,
                           ApprovalDocumentType documentType,
                           String documentId,
                           ApprovalRequestType requestType,
                           Long requesterId,
                           Long approverId,
                           String comment,
                           String reviewSnapshot) {
        this.approvalRequestId = approvalRequestId;
        this.documentType = documentType;
        this.documentId = documentId;
        this.requestType = requestType;
        this.requesterId = requesterId;
        this.approverId = approverId;
        this.comment = comment;
        this.reviewSnapshot = reviewSnapshot;
        this.requestedAt = LocalDateTime.now();
        this.status = ApprovalStatus.PENDING;
    }

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public ApprovalDocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public ApprovalRequestType getRequestType() {
        return requestType;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public Long getApproverId() {
        return approverId;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewSnapshot() {
        return reviewSnapshot;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void approve() {
        if (!ApprovalStatus.PENDING.equals(status)) {
            throw new IllegalStateException("대기 상태의 결재 요청만 승인할 수 있습니다.");
        }
        this.status = ApprovalStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject() {
        if (!ApprovalStatus.PENDING.equals(status)) {
            throw new IllegalStateException("대기 상태의 결재 요청만 반려할 수 있습니다.");
        }
        this.status = ApprovalStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
    }
}
