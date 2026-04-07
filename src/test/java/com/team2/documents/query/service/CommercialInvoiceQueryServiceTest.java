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
import com.team2.documents.query.mapper.CommercialInvoiceQueryMapper;
import com.team2.documents.query.model.CommercialInvoiceView;

@ExtendWith(MockitoExtension.class)
class CommercialInvoiceQueryServiceTest {

    @Mock
    private CommercialInvoiceQueryMapper commercialInvoiceQueryMapper;

    @InjectMocks
    private CommercialInvoiceQueryService commercialInvoiceQueryService;

    @Test
    @DisplayName("CI ID로 조회 시 해당 CI를 반환한다")
    void findById_whenCommercialInvoiceExists_thenReturnsCommercialInvoice() {
        CommercialInvoiceView ci = new CommercialInvoiceView();
        ci.setCiId("CI260001");
        ci.setStatus("CONFIRMED");
        when(commercialInvoiceQueryMapper.findById("CI260001")).thenReturn(ci);

        CommercialInvoiceView result = commercialInvoiceQueryService.findById("CI260001");

        assertEquals("CI260001", result.getCiId());
    }

    @Test
    @DisplayName("존재하지 않는 CI ID로 조회 시 예외를 던진다")
    void findById_whenCommercialInvoiceNotExists_thenThrowsException() {
        when(commercialInvoiceQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> commercialInvoiceQueryService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("전체 CI 목록을 조회한다")
    void findAll_whenCommercialInvoicesExist_thenReturnsAll() {
        CommercialInvoiceView ci = new CommercialInvoiceView();
        ci.setCiId("CI260001");
        ci.setStatus("CONFIRMED");
        when(commercialInvoiceQueryMapper.findAll()).thenReturn(List.of(ci));

        List<CommercialInvoiceView> result = commercialInvoiceQueryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
