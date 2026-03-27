package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.Collection;
import com.team2.documents.repository.CollectionRepository;

@ExtendWith(MockitoExtension.class)
class CollectionCommandServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private CollectionCommandService collectionCommandService;

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
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                "USD",
                "미수금",
                null,
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

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
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
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
}
