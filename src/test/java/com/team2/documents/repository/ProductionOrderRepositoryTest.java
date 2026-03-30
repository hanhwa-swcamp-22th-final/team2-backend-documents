package com.team2.documents.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.entity.ProductionOrder;

@DataJpaTest
class ProductionOrderRepositoryTest {

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Test
    @DisplayName("생산지시서 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenProductionOrderExists_thenReturnsEntity() {
        productionOrderRepository.save(new ProductionOrder(
                "PRD2025001",
                "PO2025001",
                null,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        ProductionOrder result = productionOrderRepository.findById("PRD2025001").orElseThrow();

        assertEquals("PRD2025001", result.getProductionOrderNo());
        assertEquals("PO2025001", result.getPoId());
    }

    @Test
    @DisplayName("생산지시서 엔티티를 수정할 수 있다")
    void update_whenProductionStatusChanges_thenPersistsUpdatedStatus() {
        productionOrderRepository.save(new ProductionOrder(
                "PRD2025002",
                "PO2025002",
                null,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        ProductionOrder productionOrder = productionOrderRepository.findById("PRD2025002").orElseThrow();
        java.lang.reflect.Field statusField;
        try {
            statusField = ProductionOrder.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(productionOrder, "생산완료");
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
        productionOrderRepository.save(productionOrder);

        ProductionOrder result = productionOrderRepository.findById("PRD2025002").orElseThrow();
        assertEquals("생산완료", result.getStatus());
    }

    @Test
    @DisplayName("생산지시서 엔티티를 삭제할 수 있다")
    void delete_whenProductionOrderExists_thenRemovesEntity() {
        ProductionOrder productionOrder = productionOrderRepository.save(new ProductionOrder(
                "PRD2025003",
                "PO2025003",
                null,
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        productionOrderRepository.delete(productionOrder);

        assertFalse(productionOrderRepository.findById("PRD2025003").isPresent());
    }

    @Test
    @DisplayName("생산지시서 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenProductionOrdersExist_thenReturnsAllEntities() {
        productionOrderRepository.save(new ProductionOrder("PRD2025100", "PO2025100", null, LocalDate.now(), null, "진행중", List.of(), LocalDateTime.now(), LocalDateTime.now()));
        productionOrderRepository.save(new ProductionOrder("PRD2025101", "PO2025101", null, LocalDate.now(), null, "진행중", List.of(), LocalDateTime.now(), LocalDateTime.now()));

        assertTrue(productionOrderRepository.findAll().size() >= 2);
    }
}
