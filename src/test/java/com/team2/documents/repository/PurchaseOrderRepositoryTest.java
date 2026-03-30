package com.team2.documents.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;

@DataJpaTest
class PurchaseOrderRepositoryTest {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    @DisplayName("PO 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenPurchaseOrderExists_thenReturnsEntity() {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);

        purchaseOrderRepository.save(purchaseOrder);

        PurchaseOrder result = purchaseOrderRepository.findById("PO2025-0001").orElseThrow();

        assertEquals("PO2025-0001", result.getPoId());
        assertEquals(PurchaseOrderStatus.DRAFT, result.getStatus());
    }

    @Test
    @DisplayName("PO 엔티티를 수정할 수 있다")
    void update_whenPurchaseOrderStatusChanges_thenPersistsUpdatedStatus() {
        purchaseOrderRepository.save(new PurchaseOrder("PO2025-0002", PurchaseOrderStatus.DRAFT));

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById("PO2025-0002").orElseThrow();
        purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(purchaseOrder);

        PurchaseOrder result = purchaseOrderRepository.findById("PO2025-0002").orElseThrow();
        assertEquals(PurchaseOrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("PO 엔티티를 삭제할 수 있다")
    void delete_whenPurchaseOrderExists_thenRemovesEntity() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(
                new PurchaseOrder("PO2025-0003", PurchaseOrderStatus.DRAFT));

        purchaseOrderRepository.delete(purchaseOrder);

        assertFalse(purchaseOrderRepository.findById("PO2025-0003").isPresent());
    }

    @Test
    @DisplayName("PO 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenPurchaseOrdersExist_thenReturnsAllEntities() {
        purchaseOrderRepository.save(new PurchaseOrder("PO2025-0010", PurchaseOrderStatus.DRAFT));
        purchaseOrderRepository.save(new PurchaseOrder("PO2025-0011", PurchaseOrderStatus.CONFIRMED));

        assertTrue(purchaseOrderRepository.findAll().size() >= 2);
    }
}
