package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.PackingListQueryMapper;
import com.team2.documents.query.model.PackingListView;

@ExtendWith(MockitoExtension.class)
class PackingListQueryServiceTest {

    @Mock
    private PackingListQueryMapper packingListQueryMapper;

    @InjectMocks
    private PackingListQueryService packingListQueryService;

    @Test
    @DisplayName("PL ID로 조회 시 해당 PL을 반환한다")
    void findById_whenPackingListExists_thenReturnsPackingList() {
        PackingListView pl = new PackingListView();
        pl.setPlId("PL260001");
        pl.setStatus("CREATED");
        when(packingListQueryMapper.findById("PL260001")).thenReturn(pl);

        PackingListView result = packingListQueryService.findById("PL260001");

        assertEquals("PL260001", result.getPlId());
    }

    @Test
    @DisplayName("존재하지 않는 PL ID로 조회 시 예외를 던진다")
    void findById_whenPackingListNotExists_thenThrowsException() {
        when(packingListQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> packingListQueryService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("전체 PL 목록을 조회한다")
    void findAll_whenPackingListsExist_thenReturnsAll() {
        PackingListView pl = new PackingListView();
        pl.setPlId("PL260001");
        pl.setStatus("CREATED");
        when(packingListQueryMapper.findAll()).thenReturn(List.of(pl));

        List<PackingListView> result = packingListQueryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
