package com.team2.documents.command.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    private static final String KRW = "KRW";

    public BigDecimal convertFromKrw(String targetCurrencyCode, BigDecimal exchangeRate, BigDecimal krwAmount) {
        if (krwAmount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = resolveRate(targetCurrencyCode, exchangeRate);
        return krwAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveRate(String targetCurrencyCode, BigDecimal exchangeRate) {
        String normalizedTargetCurrencyCode = normalizeCurrencyCode(targetCurrencyCode);
        if (KRW.equals(normalizedTargetCurrencyCode)) {
            return BigDecimal.ONE;
        }
        if (exchangeRate == null) {
            throw new IllegalArgumentException("외화 환산에는 exchangeRate가 필요합니다.");
        }
        return exchangeRate;
    }

    private String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return KRW;
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
