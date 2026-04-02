package com.team2.documents.command.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.command.domain.entity.Collection;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@DataJpaTest
class CollectionRepositoryTest {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    @DisplayName("매출·수금 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenCollectionExists_thenReturnsEntity() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260001", PurchaseOrderStatus.DRAFT));
        Collection saved = collectionRepository.save(new Collection(
                null,
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                1L,
                null,
                new BigDecimal("15000.00"),
                null,
                null,
                null,
                "미수금",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        Collection result = collectionRepository.findById(saved.getCollectionId()).orElseThrow();

        assertEquals("PO260001", result.getPoId());
        assertEquals("미수금", result.getStatus());
    }

    @Test
    @DisplayName("매출·수금 엔티티를 수정할 수 있다")
    void update_whenCollectionStatusChanges_thenPersistsUpdatedStatus() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260002", PurchaseOrderStatus.DRAFT));
        Collection collection = collectionRepository.save(new Collection(
                null,
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                1L,
                null,
                new BigDecimal("12000.00"),
                null,
                null,
                null,
                "미수금",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        collection.setStatus("수금완료");
        collection.setCollectionDate(LocalDate.of(2026, 3, 30));
        collectionRepository.save(collection);

        Collection result = collectionRepository.findById(collection.getCollectionId()).orElseThrow();
        assertEquals("수금완료", result.getStatus());
        assertEquals(LocalDate.of(2026, 3, 30), result.getCollectionDate());
    }

    @Test
    @DisplayName("매출·수금 엔티티를 삭제할 수 있다")
    void delete_whenCollectionExists_thenRemovesEntity() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260003", PurchaseOrderStatus.DRAFT));
        Collection collection = collectionRepository.save(new Collection(
                null,
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                1L,
                null,
                new BigDecimal("1000.00"),
                null,
                null,
                null,
                "미수금",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        collectionRepository.delete(collection);

        assertFalse(collectionRepository.findById(collection.getCollectionId()).isPresent());
    }

    @Test
    @DisplayName("매출·수금 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenCollectionsExist_thenReturnsAllEntities() {
        PurchaseOrder purchaseOrder1 = purchaseOrderRepository.save(new PurchaseOrder("PO260100", PurchaseOrderStatus.DRAFT));
        PurchaseOrder purchaseOrder2 = purchaseOrderRepository.save(new PurchaseOrder("PO260101", PurchaseOrderStatus.DRAFT));
        collectionRepository.save(new Collection(null, purchaseOrder1.getPurchaseOrderId(), purchaseOrder1.getPoId(), 1L, null, BigDecimal.ONE, null, null, null, "미수금", null, LocalDateTime.now(), LocalDateTime.now()));
        collectionRepository.save(new Collection(null, purchaseOrder2.getPurchaseOrderId(), purchaseOrder2.getPoId(), 1L, null, BigDecimal.TEN, null, null, null, "미수금", null, LocalDateTime.now(), LocalDateTime.now()));

        assertThat(collectionRepository.findAll())
                .extracting(Collection::getPoId, Collection::getStatus)
                .contains(
                        org.assertj.core.groups.Tuple.tuple("PO260100", "미수금"),
                        org.assertj.core.groups.Tuple.tuple("PO260101", "미수금")
                );
    }
}
