package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.query.mapper.PurchaseOrderQueryMapper;

@Service
public class PurchaseOrderQueryService {

    private final PurchaseOrderQueryMapper purchaseOrderQueryMapper;
    private final UserPositionRepository userPositionRepository;

    public PurchaseOrderQueryService(PurchaseOrderQueryMapper purchaseOrderQueryMapper,
                                     UserPositionRepository userPositionRepository) {
        this.purchaseOrderQueryMapper = purchaseOrderQueryMapper;
        this.userPositionRepository = userPositionRepository;
    }

    public PurchaseOrderStatus determineInitialStatus(Long userId) {
        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            return PurchaseOrderStatus.CONFIRMED;
        }
        return PurchaseOrderStatus.APPROVAL_PENDING;
    }

    public PurchaseOrder findById(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderQueryMapper.findById(poId);
        if (purchaseOrder == null) {
            throw new IllegalArgumentException("PO 정보를 찾을 수 없습니다.");
        }
        return purchaseOrder;
    }

    public List<PurchaseOrder> findAll() {
        return purchaseOrderQueryMapper.findAll();
    }
}
