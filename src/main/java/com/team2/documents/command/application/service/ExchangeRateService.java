package com.team2.documents.command.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.team2.documents.command.infrastructure.client.CurrencyApiClient;

@Service
public class ExchangeRateService {

    private static final String KRW = "KRW";

    private final CurrencyApiClient currencyApiClient;

    public ExchangeRateService(CurrencyApiClient currencyApiClient) {
        this.currencyApiClient = currencyApiClient;
    }

    public BigDecimal getRateFromKrw(LocalDate issueDate, String targetCurrencyCode) {
        String normalizedTargetCurrencyCode = normalizeCurrencyCode(targetCurrencyCode);
        if (KRW.equals(normalizedTargetCurrencyCode)) {
            return BigDecimal.ONE;
        }
        return currencyApiClient.getRate(issueDate, KRW, normalizedTargetCurrencyCode);
    }

    public BigDecimal convertFromKrw(LocalDate issueDate, String targetCurrencyCode, BigDecimal krwAmount) {
        if (krwAmount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = getRateFromKrw(issueDate, targetCurrencyCode);
        return krwAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return KRW;
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
