package com.team2.documents.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.entity.Collection;

@DataJpaTest
class CollectionRepositoryTest {

    @Autowired
    private CollectionRepository collectionRepository;

    @Test
    @DisplayName("매출·수금 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenCollectionExists_thenReturnsEntity() {
        Collection saved = collectionRepository.save(new Collection(
                null,
                "PO2025001",
                null,
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

        Collection result = collectionRepository.findById(saved.getId()).orElseThrow();

        assertEquals("PO2025001", result.getPoId());
        assertEquals("미수금", result.getStatus());
    }

    @Test
    @DisplayName("매출·수금 엔티티를 수정할 수 있다")
    void update_whenCollectionStatusChanges_thenPersistsUpdatedStatus() {
        Collection collection = collectionRepository.save(new Collection(
                null,
                "PO2025002",
                null,
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

        Collection result = collectionRepository.findById(collection.getId()).orElseThrow();
        assertEquals("수금완료", result.getStatus());
        assertEquals(LocalDate.of(2026, 3, 30), result.getCollectionDate());
    }

    @Test
    @DisplayName("매출·수금 엔티티를 삭제할 수 있다")
    void delete_whenCollectionExists_thenRemovesEntity() {
        Collection collection = collectionRepository.save(new Collection(
                null,
                "PO2025003",
                null,
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

        assertFalse(collectionRepository.findById(collection.getId()).isPresent());
    }

    @Test
    @DisplayName("매출·수금 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenCollectionsExist_thenReturnsAllEntities() {
        collectionRepository.save(new Collection(null, "PO2025100", null, 1L, null, BigDecimal.ONE, null, null, null, "미수금", null, LocalDateTime.now(), LocalDateTime.now()));
        collectionRepository.save(new Collection(null, "PO2025101", null, 1L, null, BigDecimal.TEN, null, null, null, "미수금", null, LocalDateTime.now(), LocalDateTime.now()));

        assertTrue(collectionRepository.findAll().size() >= 2);
    }
}
