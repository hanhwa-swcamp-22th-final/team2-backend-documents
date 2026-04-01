package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.command.domain.entity.CommercialInvoice;

@Mapper
public interface CommercialInvoiceQueryMapper {
    CommercialInvoice findById(String ciId);
    List<CommercialInvoice> findAll();
}
