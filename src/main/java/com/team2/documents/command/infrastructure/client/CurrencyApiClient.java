package com.team2.documents.command.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class CurrencyApiClient {

    private final RestClient restClient;
    private final CurrencyApiProperties currencyApiProperties;

    public CurrencyApiClient(RestClient.Builder restClientBuilder, CurrencyApiProperties currencyApiProperties) {
        this.restClient = restClientBuilder.build();
        this.currencyApiProperties = currencyApiProperties;
    }

    public BigDecimal getRate(LocalDate date, String baseCurrencyCode, String targetCurrencyCode) {
        validateEnabled();

        String base = normalizeCurrencyCode(baseCurrencyCode);
        String target = normalizeCurrencyCode(targetCurrencyCode);
        if (base.equals(target)) {
            return BigDecimal.ONE;
        }

        String dateSegment = date == null ? "latest" : date.toString();
        try {
            return extractRate(fetchPrimary(dateSegment, base), base, target);
        } catch (RuntimeException primaryException) {
            if (!currencyApiProperties.isFallbackEnabled()) {
                throw primaryException;
            }
            return extractRate(fetchFallback(dateSegment, base), base, target);
        }
    }

    private JsonNode fetchPrimary(String dateSegment, String baseCurrencyCode) {
        String url = currencyApiProperties.getPrimaryBaseUrl().replaceAll("/$", "")
                + "@"
                + dateSegment
                + "/v1/currencies/"
                + baseCurrencyCode
                + ".json";
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(JsonNode.class);
    }

    private JsonNode fetchFallback(String dateSegment, String baseCurrencyCode) {
        String url = "https://"
                + dateSegment
                + ".currency-api.pages.dev/v1/currencies/"
                + baseCurrencyCode
                + ".json";
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(JsonNode.class);
    }

    private BigDecimal extractRate(JsonNode response, String baseCurrencyCode, String targetCurrencyCode) {
        if (response == null) {
            throw new IllegalStateException("환율 API 응답이 비어 있습니다.");
        }
        JsonNode rateNode = response.path(baseCurrencyCode).path(targetCurrencyCode);
        if (rateNode.isMissingNode() || rateNode.isNull()) {
            throw new IllegalStateException("환율 API 응답에서 통화 환율을 찾을 수 없습니다: " + targetCurrencyCode);
        }
        return rateNode.decimalValue();
    }

    private void validateEnabled() {
        if (!currencyApiProperties.isEnabled()) {
            throw new IllegalStateException("currency.api.enabled 설정이 필요합니다.");
        }
    }

    private String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("통화코드는 비어 있을 수 없습니다.");
        }
        return currencyCode.trim().toLowerCase(Locale.ROOT);
    }
}
