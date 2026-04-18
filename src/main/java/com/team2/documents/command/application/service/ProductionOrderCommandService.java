package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;

@Service
@Transactional
public class ProductionOrderCommandService {

    private final ProductionOrderRepository productionOrderRepository;

    public ProductionOrderCommandService(ProductionOrderRepository productionOrderRepository) {
        this.productionOrderRepository = productionOrderRepository;
    }

    public List<ProductionOrder> findAll() {
        return productionOrderRepository.findAll();
    }

    public ProductionOrder findById(String id) {
        return productionOrderRepository.findByProductionOrderCode(id)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다."));
    }

    public ProductionOrder save(ProductionOrder productionOrder) {
        return productionOrderRepository.save(productionOrder);
    }

    /** 생산완료 처리. DB enum 값 'completed' 로 상태 변경. */
    public ProductionOrder complete(String productionOrderCode) {
        return complete(productionOrderCode, null);
    }

    /**
     * 담당자 본인 체크를 포함한 생산완료 처리.
     * - userId 가 null 이면 기존 동작 유지 (ADMIN 등 호출자 컨텍스트 없는 경로 호환).
     * - userId 가 지정되고 managerId 가 설정된 지시서면 managerId == userId 일 때만 허용.
     *   담당자 미지정(null)이면 기존처럼 role 체크로 통과 (컨트롤러 레벨 @PreAuthorize 책임).
     */
    public ProductionOrder complete(String productionOrderCode, Long userId) {
        ProductionOrder po = productionOrderRepository.findByProductionOrderCode(productionOrderCode)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다."));
        if (userId != null && po.getManagerId() != null && !po.getManagerId().equals(userId)) {
            throw new IllegalStateException("지정된 담당자만 생산완료 처리할 수 있습니다.");
        }
        po.setStatus("completed");
        return productionOrderRepository.save(po);
    }
}
