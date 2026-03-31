package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.entity.ProformaInvoice;

@Mapper
public interface ProformaInvoiceQueryMapper {

    ProformaInvoice findById(String piId);

    List<ProformaInvoice> findAll();
}
