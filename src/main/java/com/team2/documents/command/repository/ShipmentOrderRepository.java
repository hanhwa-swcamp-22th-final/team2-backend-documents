package com.team2.documents.command.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShipmentOrderRepository {

    void createFromPurchaseOrder(String poId);
}
