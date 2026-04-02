package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.CommercialInvoiceQueryMapper;
import com.team2.documents.query.model.CommercialInvoiceView;

@Service
public class CommercialInvoiceQueryService {

    private final CommercialInvoiceQueryMapper commercialInvoiceQueryMapper;

    public CommercialInvoiceQueryService(CommercialInvoiceQueryMapper commercialInvoiceQueryMapper) {
        this.commercialInvoiceQueryMapper = commercialInvoiceQueryMapper;
    }

    public CommercialInvoiceView findById(String ciId) {
        CommercialInvoiceView commercialInvoice = commercialInvoiceQueryMapper.findById(ciId);
        if (commercialInvoice == null) {
            throw new ResourceNotFoundException("CI 정보를 찾을 수 없습니다.");
        }
        return commercialInvoice;
    }

    public List<CommercialInvoiceView> findAll() {
        return commercialInvoiceQueryMapper.findAll();
    }
}
