package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.ProformaInvoiceView;

@Mapper
public interface ProformaInvoiceQueryMapper {

    ProformaInvoiceView findById(String piId);

    List<ProformaInvoiceView> findAll();

    List<ProformaInvoiceView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();
}
