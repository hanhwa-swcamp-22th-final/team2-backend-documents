package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.CollectionQueryMapper;
import com.team2.documents.query.model.CollectionView;

@ExtendWith(MockitoExtension.class)
class CollectionQueryServiceTest {

    @Mock
    private CollectionQueryMapper collectionQueryMapper;

    @InjectMocks
    private CollectionQueryService collectionQueryService;

    @Test
    @DisplayName("ID로 매출수금 현황을 조회한다")
    void findById_whenCollectionExists_thenReturnsCollection() {
        // given
        CollectionView collection = new CollectionView();
        collection.setCollectionId(1L);
        collection.setPoId("PO2025-0001");
        collection.setPoNo("PO-2026-001");
        collection.setClientId(1L);
        collection.setClientName("ABC Trading");
        collection.setTotalAmount(new BigDecimal("15000.00"));
        collection.setCollectedAmount(new BigDecimal("10000.00"));
        collection.setRemainingAmount(new BigDecimal("5000.00"));
        collection.setCurrencyCode("USD");
        collection.setStatus("미수금");
        collection.setCollectionDate(LocalDate.of(2026, 5, 15));
        collection.setCreatedAt(LocalDateTime.of(2026, 3, 5, 9, 0));
        collection.setUpdatedAt(LocalDateTime.of(2026, 5, 15, 14, 0));
        when(collectionQueryMapper.findById(1L)).thenReturn(collection);

        // when
        CollectionView result = collectionQueryService.findById(1L);

        // then
        assertEquals(1L, result.getCollectionId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 매출수금 현황 조회 시 예외를 던진다")
    void findById_whenCollectionNotExists_thenThrowsException() {
        // given
        when(collectionQueryMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> collectionQueryService.findById(999L));
    }

    @Test
    @DisplayName("전체 매출수금 현황 목록을 조회한다")
    void findAll_whenCollectionsExist_thenReturnsAll() {
        // given
        CollectionView collection = new CollectionView();
        collection.setCollectionId(1L);
        collection.setPoId("PO2025-0001");
        collection.setPoNo("PO-2026-001");
        collection.setClientId(1L);
        collection.setClientName("ABC Trading");
        collection.setTotalAmount(new BigDecimal("15000.00"));
        collection.setCollectedAmount(new BigDecimal("10000.00"));
        collection.setRemainingAmount(new BigDecimal("5000.00"));
        collection.setCurrencyCode("USD");
        collection.setStatus("미수금");
        collection.setCollectionDate(LocalDate.of(2026, 5, 15));
        collection.setCreatedAt(LocalDateTime.of(2026, 3, 5, 9, 0));
        collection.setUpdatedAt(LocalDateTime.of(2026, 5, 15, 14, 0));
        when(collectionQueryMapper.findAll()).thenReturn(List.of(collection));

        // when
        List<CollectionView> result = collectionQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
