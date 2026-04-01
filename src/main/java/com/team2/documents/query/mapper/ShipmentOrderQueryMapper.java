package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.command.domain.entity.ShipmentOrder;

@Mapper
public interface ShipmentOrderQueryMapper {
    ShipmentOrder findById(String shipmentOrderId);
    List<ShipmentOrder> findAll();
}
