package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.team2.documents.entity.Collection;
import com.team2.documents.repository.CollectionRepository;

@ExtendWith(MockitoExtension.class)
class CollectionQueryServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionQueryService collectionQueryService;

    @Test
    @DisplayName("매출·수금 현황 목록 조회 시 전체 목록을 반환한다")
    void findAll_whenCollectionsExist_thenReturnsAllCollections() {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                new BigDecimal("15000.00"),
                new BigDecimal("10000.00"),
                new BigDecimal("5000.00"),
                "USD",
                "미수금",
                LocalDate.of(2026, 5, 15),
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionRepository.findAll()).thenReturn(List.of(collection));

        // when
        List<Collection> collections = collectionQueryService.findAll();

        // then
        assertEquals(1, collections.size());
        assertEquals("PO2025001", collections.get(0).getPoId());
        assertEquals("미수금", collections.get(0).getStatus());
    }

    @Test
    @DisplayName("매출·수금 현황 단건 조회 시 해당 현황을 반환한다")
    void findById_whenCollectionExists_thenReturnsCollection() {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                new BigDecimal("15000.00"),
                new BigDecimal("10000.00"),
                new BigDecimal("5000.00"),
                "USD",
                "미수금",
                LocalDate.of(2026, 5, 15),
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionRepository.findById(1L)).thenReturn(java.util.Optional.of(collection));

        // when
        Collection result = collectionQueryService.findById(1L);

        // then
        assertEquals("PO2025001", result.getPoId());
        assertEquals("ABC Trading", result.getClientName());
        assertEquals("미수금", result.getStatus());
    }
}
