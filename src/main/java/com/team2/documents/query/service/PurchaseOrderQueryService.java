package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.dto.PagedResult;
import com.team2.documents.query.mapper.PurchaseOrderQueryMapper;
import com.team2.documents.query.model.PurchaseOrderView;

@Service
public class PurchaseOrderQueryService {

    private final PurchaseOrderQueryMapper purchaseOrderQueryMapper;

    public PurchaseOrderQueryService(PurchaseOrderQueryMapper purchaseOrderQueryMapper) {
        this.purchaseOrderQueryMapper = purchaseOrderQueryMapper;
    }

    public PurchaseOrderStatus determineInitialStatus(Long userId) {
        return PurchaseOrderStatus.DRAFT;
    }

    public PurchaseOrderView findById(String poId) {
        PurchaseOrderView purchaseOrder = purchaseOrderQueryMapper.findById(poId);
        if (purchaseOrder == null) {
            throw new ResourceNotFoundException("PO 정보를 찾을 수 없습니다.");
        }
        return purchaseOrder;
    }

    public List<PurchaseOrderView> findAll() {
        return purchaseOrderQueryMapper.findAll();
    }

    public PagedResult<PurchaseOrderView> findAll(int page, int size) {
        int offset = page * size;
        List<PurchaseOrderView> content = purchaseOrderQueryMapper.findPage(offset, size);
        long total = purchaseOrderQueryMapper.countAll();
        return new PagedResult<>(content, total);
    }

    /**
     * 팀 스코프 적용 페이지 조회.
     * @param managerIdScope null → 전체 (ADMIN), 빈 리스트 → 빈 페이지, 비어있지 않은 리스트 → manager_id IN (...)
     */
    public PagedResult<PurchaseOrderView> findAll(int page, int size, List<Long> managerIdScope) {
        if (managerIdScope == null) {
            return findAll(page, size);
        }
        if (managerIdScope.isEmpty()) {
            return new PagedResult<>(List.of(), 0L);
        }
        int offset = page * size;
        List<PurchaseOrderView> content = purchaseOrderQueryMapper.findPageScoped(offset, size, managerIdScope);
        long total = purchaseOrderQueryMapper.countScoped(managerIdScope);
        return new PagedResult<>(content, total);
    }
}
