package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.ShipmentView;

@Mapper
public interface ShipmentQueryMapper {

    ShipmentView findById(Long shipmentId);

    List<ShipmentView> findAll();

    ShipmentView findByPoId(String poId);

    List<ShipmentView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();
}
