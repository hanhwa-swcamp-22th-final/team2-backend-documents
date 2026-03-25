package com.team2.documents.repository;

public interface ApprovalRequestRepository {

    void createForPurchaseOrder(Long userId);

    void createForProformaInvoice(String piId, Long userId);
}
