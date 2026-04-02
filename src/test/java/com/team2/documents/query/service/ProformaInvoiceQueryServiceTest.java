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

import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.ProformaInvoiceQueryMapper;
import com.team2.documents.query.model.ProformaInvoiceView;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceQueryServiceTest {

    @Mock
    private ProformaInvoiceQueryMapper proformaInvoiceQueryMapper;

    @InjectMocks
    private ProformaInvoiceQueryService proformaInvoiceQueryService;

    @Test
    @DisplayName("PI ID로 조회 시 해당 PI를 반환한다")
    void findById_whenProformaInvoiceExists_thenReturnsProformaInvoice() {
        // given
        ProformaInvoiceView pi = new ProformaInvoiceView();
        pi.setPiId("PI2025-0001");
        pi.setStatus(ProformaInvoiceStatus.DRAFT.name());
        when(proformaInvoiceQueryMapper.findById("PI2025-0001")).thenReturn(pi);

        // when
        ProformaInvoiceView result = proformaInvoiceQueryService.findById("PI2025-0001");

        // then
        assertEquals("PI2025-0001", result.getPiId());
    }

    @Test
    @DisplayName("존재하지 않는 PI ID로 조회 시 예외를 던진다")
    void findById_whenProformaInvoiceNotExists_thenThrowsException() {
        // given
        when(proformaInvoiceQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> proformaInvoiceQueryService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("전체 PI 목록을 조회한다")
    void findAll_whenProformaInvoicesExist_thenReturnsAll() {
        // given
        ProformaInvoiceView pi = new ProformaInvoiceView();
        pi.setPiId("PI2025-0001");
        pi.setStatus(ProformaInvoiceStatus.DRAFT.name());
        when(proformaInvoiceQueryMapper.findAll()).thenReturn(List.of(pi));

        // when
        List<ProformaInvoiceView> result = proformaInvoiceQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
