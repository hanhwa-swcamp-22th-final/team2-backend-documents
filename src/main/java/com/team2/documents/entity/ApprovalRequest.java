package com.team2.documents.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.ApprovalStatus;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_request_id")
    private Long approvalRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_document_type", nullable = false)
    private ApprovalDocumentType documentType;

    @Column(name = "approval_document_id", nullable = false, length = 30)
    private String documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_request_type", nullable = false)
    private ApprovalRequestType requestType;

    @Column(name = "approval_requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "approval_approver_id", nullable = false)
    private Long approverId;

    @Column(name = "approval_comment")
    private String comment;

    @Column(name = "approval_review_snapshot", columnDefinition = "TEXT")
    private String reviewSnapshot;

    @Column(name = "approval_requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approval_reviewed_at")
    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus status;

    protected ApprovalRequest() {
    }

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
        this.status = ApprovalStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
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

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    @PrePersist
    void prePersist() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ApprovalStatus.PENDING;
        }
    }
}
