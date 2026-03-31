package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.ProductionOrder;
import com.team2.documents.mapper.ProductionOrderQueryMapper;

@ExtendWith(MockitoExtension.class)
class ProductionOrderQueryServiceTest {

    @Mock
    private ProductionOrderQueryMapper productionOrderQueryMapper;

    @InjectMocks
    private ProductionOrderQueryService productionOrderQueryService;

    @Test
    @DisplayName("생산지시서 목록 조회 시 전체 목록을 반환한다")
    void findAll_whenProductionOrdersExist_thenReturnsAllProductionOrders() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "PRD-2026-001", "PO2025001", null,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(), null, null);
        when(productionOrderQueryMapper.findAll()).thenReturn(List.of(productionOrder));

        // when
        List<ProductionOrder> productionOrders = productionOrderQueryService.findAll();

        // then
        assertEquals(1, productionOrders.size());
        assertEquals("PRD-2026-001", productionOrders.get(0).getProductionOrderId());
    }

    @Test
    @DisplayName("생산지시서 단건 조회 시 해당 생산지시서를 반환한다")
    void findById_whenProductionOrderExists_thenReturnsProductionOrder() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "PRD-2026-001", "PO2025001", null,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(), null, null);
        when(productionOrderQueryMapper.findById("PRD-2026-001")).thenReturn(productionOrder);

        // when
        ProductionOrder result = productionOrderQueryService.findById("PRD-2026-001");

        // then
        assertEquals("PRD-2026-001", result.getProductionOrderId());
        assertEquals("PO2025001", result.getPoId());
    }

    @Test
    @DisplayName("존재하지 않는 생산지시서 조회 시 예외를 던진다")
    void findById_whenProductionOrderNotExists_thenThrowsException() {
        // given
        when(productionOrderQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productionOrderQueryService.findById("NOT-EXIST"));
    }
}
