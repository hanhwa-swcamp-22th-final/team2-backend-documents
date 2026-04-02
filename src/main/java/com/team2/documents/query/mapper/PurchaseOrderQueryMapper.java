package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.query.model.PurchaseOrderView;

@Mapper
public interface PurchaseOrderQueryMapper {

    PurchaseOrderView findById(String poId);

    List<PurchaseOrderView> findAll();
}
