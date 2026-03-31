package com.team2.documents.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.entity.Shipment;

@Mapper
public interface ShipmentQueryMapper {

    Shipment findById(Long shipmentId);

    List<Shipment> findAll();

    Shipment findByPoId(String poId);
}
