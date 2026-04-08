package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class ExchangeRateServiceTest {

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Test
    @DisplayName("KRW 대상 환산은 환율 없이 원 금액을 그대로 반환한다")
    void convertFromKrw_whenTargetCurrencyIsKrw_thenReturnsOriginalAmount() {
        BigDecimal result = exchangeRateService.convertFromKrw("KRW", null, new BigDecimal("10000"));

        assertTrue(new BigDecimal("10000").compareTo(result) == 0);
    }

    @Test
    @DisplayName("외화 환산은 프론트에서 전달한 환율을 곱해 계산한다")
    void convertFromKrw_whenTargetCurrencyIsUsd_thenUsesFrontendExchangeRate() {
        BigDecimal result = exchangeRateService.convertFromKrw("USD", new BigDecimal("0.00073"), new BigDecimal("10000"));

        assertEquals(new BigDecimal("7.30"), result);
    }

    @Test
    @DisplayName("외화 환산인데 환율이 없으면 예외가 발생한다")
    void convertFromKrw_whenTargetCurrencyIsForeignAndRateMissing_thenThrows() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> exchangeRateService.convertFromKrw("USD", null, new BigDecimal("10000"))
        );

        assertEquals("외화 환산에는 exchangeRate가 필요합니다.", exception.getMessage());
    }
}
