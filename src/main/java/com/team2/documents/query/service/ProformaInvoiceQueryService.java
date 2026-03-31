package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.query.mapper.ProformaInvoiceQueryMapper;

@Service
public class ProformaInvoiceQueryService {

    private final ProformaInvoiceQueryMapper proformaInvoiceQueryMapper;

    public ProformaInvoiceQueryService(ProformaInvoiceQueryMapper proformaInvoiceQueryMapper) {
        this.proformaInvoiceQueryMapper = proformaInvoiceQueryMapper;
    }

    public ProformaInvoice findById(String piId) {
        ProformaInvoice proformaInvoice = proformaInvoiceQueryMapper.findById(piId);
        if (proformaInvoice == null) {
            throw new IllegalArgumentException("PI 정보를 찾을 수 없습니다.");
        }
        return proformaInvoice;
    }

    public List<ProformaInvoice> findAll() {
        return proformaInvoiceQueryMapper.findAll();
    }
}
