package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.ProductionOrder;
import com.team2.documents.repository.ProductionOrderRepository;

@ExtendWith(MockitoExtension.class)
class ProductionOrderQueryServiceTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @InjectMocks
    private ProductionOrderQueryService productionOrderQueryService;

    @Test
    @DisplayName("생산지시서 목록 조회 시 전체 목록을 반환한다")
    void findAll_whenProductionOrdersExist_thenReturnsAllProductionOrders() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                1L,
                "PRD-2026-001",
                "PO2025001",
                "PO-2026-001",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.of(2026, 3, 10, 9, 0),
                LocalDateTime.of(2026, 3, 15, 14, 0)
        );
        when(productionOrderRepository.findAll()).thenReturn(List.of(productionOrder));

        // when
        List<ProductionOrder> productionOrders = productionOrderQueryService.findAll();

        // then
        assertEquals(1, productionOrders.size());
        assertEquals("PRD-2026-001", productionOrders.get(0).getProductionOrderNo());
    }

    @Test
    @DisplayName("생산지시서 단건 조회 시 해당 생산지시서를 반환한다")
    void findById_whenProductionOrderExists_thenReturnsProductionOrder() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                1L,
                "PRD-2026-001",
                "PO2025001",
                "PO-2026-001",
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.of(2026, 3, 10, 9, 0),
                LocalDateTime.of(2026, 3, 15, 14, 0)
        );
        when(productionOrderRepository.findById(1L)).thenReturn(java.util.Optional.of(productionOrder));

        // when
        ProductionOrder result = productionOrderQueryService.findById(1L);

        // then
        assertEquals("PRD-2026-001", result.getProductionOrderNo());
        assertEquals("PO2025001", result.getPoId());
    }
}
