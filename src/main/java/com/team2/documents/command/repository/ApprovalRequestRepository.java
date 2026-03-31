package com.team2.documents.command.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalStatus;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    Optional<ApprovalRequest> findByDocumentTypeAndDocumentIdAndStatus(ApprovalDocumentType documentType,
                                                                       String documentId,
                                                                       ApprovalStatus status);

    default Optional<ApprovalRequest> findPendingByDocument(ApprovalDocumentType documentType, String documentId) {
        return findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, ApprovalStatus.PENDING);
    }
}
