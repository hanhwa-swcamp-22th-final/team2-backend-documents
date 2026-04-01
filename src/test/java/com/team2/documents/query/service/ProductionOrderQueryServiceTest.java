package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.query.mapper.ProductionOrderQueryMapper;

@ExtendWith(MockitoExtension.class)
class ProductionOrderQueryServiceTest {

    @Mock
    private ProductionOrderQueryMapper productionOrderQueryMapper;

    @InjectMocks
    private ProductionOrderQueryService productionOrderQueryService;

    @Test
    @DisplayName("ID로 생산지시서를 조회한다")
    void findById_whenProductionOrderExists_thenReturnsProductionOrder() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "PRD-2026-001", "PO2025-0001", "PO-2026-001",
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(),
                LocalDateTime.of(2026, 3, 10, 9, 0), LocalDateTime.of(2026, 3, 15, 14, 0));
        when(productionOrderQueryMapper.findById("PRD-2026-001")).thenReturn(productionOrder);

        // when
        ProductionOrder result = productionOrderQueryService.findById("PRD-2026-001");

        // then
        assertEquals("PRD-2026-001", result.getProductionOrderId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 생산지시서 조회 시 예외를 던진다")
    void findById_whenProductionOrderNotExists_thenThrowsException() {
        // given
        when(productionOrderQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productionOrderQueryService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("전체 생산지시서 목록을 조회한다")
    void findAll_whenProductionOrdersExist_thenReturnsAll() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "PRD-2026-001", "PO2025-0001", "PO-2026-001",
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(),
                LocalDateTime.of(2026, 3, 10, 9, 0), LocalDateTime.of(2026, 3, 15, 14, 0));
        when(productionOrderQueryMapper.findAll()).thenReturn(List.of(productionOrder));

        // when
        List<ProductionOrder> result = productionOrderQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
