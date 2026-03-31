package com.team2.documents.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.Collection;
import com.team2.documents.command.repository.CollectionRepository;

@ExtendWith(MockitoExtension.class)
class CollectionCommandServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionCommandService collectionCommandService;

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
        List<Collection> collections = collectionCommandService.findAll();

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
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        // when
        Collection result = collectionCommandService.findById(1L);

        // then
        assertEquals("PO2025001", result.getPoId());
        assertEquals("ABC Trading", result.getClientName());
        assertEquals("미수금", result.getStatus());
    }

    @Test
    @DisplayName("미수금 상태를 수금완료로 변경하면 수금일이 기록된다")
    void complete_whenCollectionIsUncollected_thenChangesStatusAndCompletedDate() {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "USD",
                "미수금",
                null,
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Collection result = collectionCommandService.complete(1L, "수금완료", LocalDate.of(2026, 6, 1));

        // then
        assertEquals("수금완료", result.getStatus());
        assertEquals(LocalDate.of(2026, 6, 1), result.getCollectionDate());
    }

    @Test
    @DisplayName("수금완료가 아닌 상태값으로 수금 처리하면 예외가 발생한다")
    void complete_whenStatusIsNotCollected_thenThrowsException() {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "USD",
                "미수금",
                null,
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        // when
        assertThrows(IllegalArgumentException.class,
                () -> collectionCommandService.complete(1L, "미수금", LocalDate.of(2026, 6, 1)));
    }

    @Test
    @DisplayName("매출·수금 현황 정보가 없으면 수금 처리 시 예외가 발생한다")
    void complete_whenCollectionDoesNotExist_thenThrowsException() {
        // given
        when(collectionRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> collectionCommandService.complete(1L, "수금완료", LocalDate.of(2026, 6, 1)));
    }

    @Test
    @DisplayName("이미 수금완료 상태인 현황을 다시 수금 처리하면 예외가 발생한다")
    void complete_whenCollectionAlreadyCompleted_thenThrowsException() {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "USD",
                "수금완료",
                LocalDate.of(2026, 6, 1),
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> collectionCommandService.complete(1L, "수금완료", LocalDate.of(2026, 6, 2)));
    }
}
