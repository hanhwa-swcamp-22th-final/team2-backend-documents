package com.team2.documents.repository;

public interface ShipmentOrderRepository {

    void createFromPurchaseOrder(String poId);
}
