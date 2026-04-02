package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.query.model.ProductionOrderView;

@Mapper
public interface ProductionOrderQueryMapper {

    ProductionOrderView findById(String productionOrderId);

    List<ProductionOrderView> findAll();
}
