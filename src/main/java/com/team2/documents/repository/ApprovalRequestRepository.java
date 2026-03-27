package com.team2.documents.repository;

import java.util.Optional;

import com.team2.documents.entity.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequest;

public interface ApprovalRequestRepository {

    void createForPurchaseOrder(Long userId);

    void createForProformaInvoice(String piId, Long userId);

    Optional<ApprovalRequest> findPendingByDocument(ApprovalDocumentType documentType, String documentId);

    Optional<ApprovalRequest> findById(Long id);

    ApprovalRequest save(ApprovalRequest approvalRequest);
}
