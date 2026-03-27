package com.team2.documents.repository;

public interface PackingListRepository {

    void createFromPurchaseOrder(String poId);
}
