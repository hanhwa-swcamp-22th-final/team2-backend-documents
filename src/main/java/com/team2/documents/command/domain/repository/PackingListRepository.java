package com.team2.documents.command.domain.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PackingListRepository {

    void createFromPurchaseOrder(String poId, String plId);
}
