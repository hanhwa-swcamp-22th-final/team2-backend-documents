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

import com.team2.documents.command.domain.entity.Collection;
import com.team2.documents.query.mapper.CollectionQueryMapper;

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
        Collection collection = new Collection(1L, "PO2025-0001", "PO-2026-001", 1L, "ABC Trading",
                new BigDecimal("15000.00"), new BigDecimal("10000.00"), new BigDecimal("5000.00"),
                "USD", "미수금", LocalDate.of(2026, 5, 15),
                LocalDateTime.of(2026, 3, 5, 9, 0), LocalDateTime.of(2026, 5, 15, 14, 0));
        when(collectionQueryMapper.findById(1L)).thenReturn(collection);

        // when
        Collection result = collectionQueryService.findById(1L);

        // then
        assertEquals(1L, result.getCollectionId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 매출수금 현황 조회 시 예외를 던진다")
    void findById_whenCollectionNotExists_thenThrowsException() {
        // given
        when(collectionQueryMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> collectionQueryService.findById(999L));
    }

    @Test
    @DisplayName("전체 매출수금 현황 목록을 조회한다")
    void findAll_whenCollectionsExist_thenReturnsAll() {
        // given
        Collection collection = new Collection(1L, "PO2025-0001", "PO-2026-001", 1L, "ABC Trading",
                new BigDecimal("15000.00"), new BigDecimal("10000.00"), new BigDecimal("5000.00"),
                "USD", "미수금", LocalDate.of(2026, 5, 15),
                LocalDateTime.of(2026, 3, 5, 9, 0), LocalDateTime.of(2026, 5, 15, 14, 0));
        when(collectionQueryMapper.findAll()).thenReturn(List.of(collection));

        // when
        List<Collection> result = collectionQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
