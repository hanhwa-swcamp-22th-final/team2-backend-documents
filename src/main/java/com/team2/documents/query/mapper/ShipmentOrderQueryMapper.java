package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.query.model.ShipmentOrderView;

@Mapper
public interface ShipmentOrderQueryMapper {
    ShipmentOrderView findById(String shipmentOrderId);
    List<ShipmentOrderView> findAll();
}
