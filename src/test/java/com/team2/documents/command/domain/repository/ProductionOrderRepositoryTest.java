package com.team2.documents.command.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@DataJpaTest
class ProductionOrderRepositoryTest {

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    @DisplayName("생산지시서 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenProductionOrderExists_thenReturnsEntity() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260001", PurchaseOrderStatus.DRAFT));
        productionOrderRepository.save(new ProductionOrder(
                "MO260001",
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        ProductionOrder result = productionOrderRepository.findById("MO260001").orElseThrow();

        assertEquals("MO260001", result.getProductionOrderId());
        assertEquals("PO260001", result.getPoId());
    }

    @Test
    @DisplayName("생산지시서 엔티티를 수정할 수 있다")
    void update_whenProductionStatusChanges_thenPersistsUpdatedStatus() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260002", PurchaseOrderStatus.DRAFT));
        productionOrderRepository.save(new ProductionOrder(
                "MO260002",
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        ProductionOrder productionOrder = productionOrderRepository.findById("MO260002").orElseThrow();
        java.lang.reflect.Field statusField;
        try {
            statusField = ProductionOrder.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(productionOrder, "생산완료");
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
        productionOrderRepository.save(productionOrder);

        ProductionOrder result = productionOrderRepository.findById("MO260002").orElseThrow();
        assertEquals("생산완료", result.getStatus());
    }

    @Test
    @DisplayName("생산지시서 엔티티를 삭제할 수 있다")
    void delete_whenProductionOrderExists_thenRemovesEntity() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260003", PurchaseOrderStatus.DRAFT));
        ProductionOrder productionOrder = productionOrderRepository.save(new ProductionOrder(
                "MO260003",
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 4, 10),
                "진행중",
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        productionOrderRepository.delete(productionOrder);

        assertFalse(productionOrderRepository.findById("MO260003").isPresent());
    }

    @Test
    @DisplayName("생산지시서 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenProductionOrdersExist_thenReturnsAllEntities() {
        PurchaseOrder purchaseOrder1 = purchaseOrderRepository.save(new PurchaseOrder("PO260100", PurchaseOrderStatus.DRAFT));
        PurchaseOrder purchaseOrder2 = purchaseOrderRepository.save(new PurchaseOrder("PO260101", PurchaseOrderStatus.DRAFT));
        productionOrderRepository.save(new ProductionOrder("MO260100", purchaseOrder1.getPurchaseOrderId(), purchaseOrder1.getPoId(), LocalDate.now(), null, "진행중", List.of(), LocalDateTime.now(), LocalDateTime.now()));
        productionOrderRepository.save(new ProductionOrder("MO260101", purchaseOrder2.getPurchaseOrderId(), purchaseOrder2.getPoId(), LocalDate.now(), null, "진행중", List.of(), LocalDateTime.now(), LocalDateTime.now()));

        assertThat(productionOrderRepository.findAll())
                .extracting(ProductionOrder::getProductionOrderId, ProductionOrder::getPoId)
                .contains(
                        org.assertj.core.groups.Tuple.tuple("MO260100", "PO260100"),
                        org.assertj.core.groups.Tuple.tuple("MO260101", "PO260101")
                );
    }
}
