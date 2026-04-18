package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.PurchaseOrderView;

@Mapper
public interface PurchaseOrderQueryMapper {

    PurchaseOrderView findById(String poId);

    List<PurchaseOrderView> findAll();

    List<PurchaseOrderView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    List<PurchaseOrderView> findPageScoped(@Param("offset") int offset,
                                           @Param("limit") int limit,
                                           @Param("managerIds") List<Long> managerIds);

    long countAll();

    long countScoped(@Param("managerIds") List<Long> managerIds);
}
