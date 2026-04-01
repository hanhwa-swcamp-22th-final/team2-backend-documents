package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.query.mapper.CommercialInvoiceQueryMapper;

@Service
public class CommercialInvoiceQueryService {

    private final CommercialInvoiceQueryMapper commercialInvoiceQueryMapper;

    public CommercialInvoiceQueryService(CommercialInvoiceQueryMapper commercialInvoiceQueryMapper) {
        this.commercialInvoiceQueryMapper = commercialInvoiceQueryMapper;
    }

    public CommercialInvoice findById(String ciId) {
        CommercialInvoice commercialInvoice = commercialInvoiceQueryMapper.findById(ciId);
        if (commercialInvoice == null) {
            throw new IllegalArgumentException("CI 정보를 찾을 수 없습니다.");
        }
        return commercialInvoice;
    }

    public List<CommercialInvoice> findAll() {
        return commercialInvoiceQueryMapper.findAll();
    }
}
