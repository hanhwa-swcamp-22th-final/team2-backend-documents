package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.CommercialInvoiceView;

@Mapper
public interface CommercialInvoiceQueryMapper {
    CommercialInvoiceView findById(String ciId);
    List<CommercialInvoiceView> findAll();

    List<CommercialInvoiceView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();
}
