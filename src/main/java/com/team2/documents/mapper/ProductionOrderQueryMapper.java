package com.team2.documents.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.entity.ProductionOrder;

@Mapper
public interface ProductionOrderQueryMapper {

    ProductionOrder findById(String productionOrderId);

    List<ProductionOrder> findAll();
}
