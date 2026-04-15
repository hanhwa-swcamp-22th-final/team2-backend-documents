package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.ShipmentOrderView;

@Mapper
public interface ShipmentOrderQueryMapper {
    ShipmentOrderView findById(String shipmentOrderId);
    List<ShipmentOrderView> findAll();

    List<ShipmentOrderView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();
}
