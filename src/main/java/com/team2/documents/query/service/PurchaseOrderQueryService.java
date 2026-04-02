package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.common.error.ResourceNotFoundException;
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
}
