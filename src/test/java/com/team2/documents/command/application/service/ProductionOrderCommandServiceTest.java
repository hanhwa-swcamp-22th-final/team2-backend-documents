package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;

@ExtendWith(MockitoExtension.class)
class ProductionOrderCommandServiceTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @InjectMocks
    private ProductionOrderCommandService productionOrderCommandService;

    @Test
    @DisplayName("생산지시서 목록 조회 시 전체 목록을 반환한다")
    void findAll_whenProductionOrdersExist_thenReturnsAllProductionOrders() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "MO260001", "PO260001", null,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(), null, null);
        when(productionOrderRepository.findAll()).thenReturn(List.of(productionOrder));

        // when
        List<ProductionOrder> productionOrders = productionOrderCommandService.findAll();

        // then
        assertEquals(1, productionOrders.size());
        assertEquals("MO260001", productionOrders.get(0).getProductionOrderId());
    }

    @Test
    @DisplayName("생산지시서 단건 조회 시 해당 생산지시서를 반환한다")
    void findById_whenProductionOrderExists_thenReturnsProductionOrder() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "MO260001", "PO260001", null,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(), null, null);
        when(productionOrderRepository.findByProductionOrderCode("MO260001")).thenReturn(Optional.of(productionOrder));

        // when
        ProductionOrder result = productionOrderCommandService.findById("MO260001");

        // then
        assertEquals("MO260001", result.getProductionOrderId());
        assertEquals("PO260001", result.getPoId());
    }

    @Test
    @DisplayName("존재하지 않는 생산지시서 조회 시 예외를 던진다")
    void findById_whenProductionOrderNotExists_thenThrowsException() {
        // given
        when(productionOrderRepository.findByProductionOrderCode("NOT-EXIST")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> productionOrderCommandService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("생산지시서 저장 시 저장된 생산지시서를 반환한다")
    void save_whenProductionOrderIsValid_thenReturnsSavedProductionOrder() {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "MO260001", "PO260001", null,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10),
                "진행중", List.of(), null, null);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);

        // when
        ProductionOrder result = productionOrderCommandService.save(productionOrder);

        // then
        assertEquals("MO260001", result.getProductionOrderId());
    }
}
