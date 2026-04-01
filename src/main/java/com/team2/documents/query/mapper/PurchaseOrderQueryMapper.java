package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.command.domain.entity.PurchaseOrder;

@Mapper
public interface PurchaseOrderQueryMapper {

    PurchaseOrder findById(String poId);

    List<PurchaseOrder> findAll();
}
