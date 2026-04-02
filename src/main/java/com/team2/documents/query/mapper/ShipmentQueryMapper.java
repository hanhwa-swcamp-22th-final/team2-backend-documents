package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.query.model.ShipmentView;

@Mapper
public interface ShipmentQueryMapper {

    ShipmentView findById(Long shipmentId);

    List<ShipmentView> findAll();

    ShipmentView findByPoId(String poId);
}
