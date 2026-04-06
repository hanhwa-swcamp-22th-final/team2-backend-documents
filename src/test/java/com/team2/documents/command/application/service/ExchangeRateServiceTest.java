package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.infrastructure.client.CurrencyApiClient;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private CurrencyApiClient currencyApiClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    @DisplayName("KRW 대상 환산은 외부 API 호출 없이 원 금액을 그대로 반환한다")
    void convertFromKrw_whenTargetCurrencyIsKrw_thenReturnsOriginalAmount() {
        BigDecimal result = exchangeRateService.convertFromKrw(LocalDate.of(2026, 4, 6), "KRW", new BigDecimal("10000"));

        assertTrue(new BigDecimal("10000").compareTo(result) == 0);
        verifyNoInteractions(currencyApiClient);
    }

    @Test
    @DisplayName("외화 환산은 PI 발행일 기준 환율을 곱해 계산한다")
    void convertFromKrw_whenTargetCurrencyIsUsd_thenUsesExternalRate() {
        when(currencyApiClient.getRate(LocalDate.of(2026, 4, 6), "KRW", "USD"))
                .thenReturn(new BigDecimal("0.00073"));

        BigDecimal result = exchangeRateService.convertFromKrw(
                LocalDate.of(2026, 4, 6),
                "USD",
                new BigDecimal("10000")
        );

        assertEquals(new BigDecimal("7.30"), result);
        verify(currencyApiClient).getRate(LocalDate.of(2026, 4, 6), "KRW", "USD");
    }
}
