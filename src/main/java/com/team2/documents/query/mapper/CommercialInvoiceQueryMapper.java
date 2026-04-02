package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.query.model.CommercialInvoiceView;

@Mapper
public interface CommercialInvoiceQueryMapper {
    CommercialInvoiceView findById(String ciId);
    List<CommercialInvoiceView> findAll();
}
