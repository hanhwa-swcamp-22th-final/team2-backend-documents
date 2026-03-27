package com.team2.documents.repository;

public interface CommercialInvoiceRepository {

    void createFromPurchaseOrder(String poId);
}
