package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.dto.PagedResult;
import com.team2.documents.query.mapper.ProformaInvoiceQueryMapper;
import com.team2.documents.query.model.ProformaInvoiceView;

@Service
public class ProformaInvoiceQueryService {

    private final ProformaInvoiceQueryMapper proformaInvoiceQueryMapper;

    public ProformaInvoiceQueryService(ProformaInvoiceQueryMapper proformaInvoiceQueryMapper) {
        this.proformaInvoiceQueryMapper = proformaInvoiceQueryMapper;
    }

    public ProformaInvoiceView findById(String piId) {
        ProformaInvoiceView proformaInvoice = proformaInvoiceQueryMapper.findById(piId);
        if (proformaInvoice == null) {
            throw new ResourceNotFoundException("PI 정보를 찾을 수 없습니다.");
        }
        return proformaInvoice;
    }

    public List<ProformaInvoiceView> findAll() {
        return proformaInvoiceQueryMapper.findAll();
    }

    public PagedResult<ProformaInvoiceView> findAll(int page, int size) {
        int offset = page * size;
        List<ProformaInvoiceView> content = proformaInvoiceQueryMapper.findPage(offset, size);
        long total = proformaInvoiceQueryMapper.countAll();
        return new PagedResult<>(content, total);
    }

    /**
     * 팀 스코프 적용 페이지 조회.
     * @param managerIdScope null → 전체 (ADMIN), 빈 리스트 → 빈 페이지, 비어있지 않은 리스트 → manager_id IN (...)
     */
    public PagedResult<ProformaInvoiceView> findAll(int page, int size, List<Long> managerIdScope) {
        if (managerIdScope == null) {
            return findAll(page, size);
        }
        if (managerIdScope.isEmpty()) {
            return new PagedResult<>(List.of(), 0L);
        }
        int offset = page * size;
        List<ProformaInvoiceView> content = proformaInvoiceQueryMapper.findPageScoped(offset, size, managerIdScope);
        long total = proformaInvoiceQueryMapper.countScoped(managerIdScope);
        return new PagedResult<>(content, total);
    }
}
