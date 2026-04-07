package com.team2.documents.command.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.team2.documents.config.CurrencyApiProperties;

class CurrencyApiClientTest {

    private CurrencyApiProperties currencyApiProperties;
    private CurrencyApiClient currencyApiClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        currencyApiProperties = new CurrencyApiProperties();
        Clock fixedClock = Clock.fixed(Instant.parse("2026-04-07T00:00:00Z"), ZoneOffset.UTC);
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        currencyApiClient = new CurrencyApiClient(builder, currencyApiProperties, fixedClock);
    }

    @Test
    @DisplayName("과거 날짜 환율 조회는 primary API에서 USD 환율을 반환한다")
    void getRate_whenHistoricalUsdRequest_thenReturnsUsdRateFromPrimary() {
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2026-04-06/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-06","krw":{"usd":0.00073,"jpy":0.11,"gbp":0.00058}}
                        """, MediaType.APPLICATION_JSON));

        BigDecimal rate = currencyApiClient.getRate(LocalDate.of(2026, 4, 6), "KRW", "USD");

        assertThat(rate).isEqualByComparingTo("0.00073");
        mockServer.verify();
    }

    @Test
    @DisplayName("과거 날짜 환율 조회는 JPY와 GBP 같은 다른 기축통화도 파싱한다")
    void getRate_whenHistoricalMajorCurrencyRequest_thenParsesJpyAndGbpRates() {
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2026-04-05/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-05","krw":{"usd":0.00072,"jpy":0.10900,"gbp":0.00057}}
                        """, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2026-04-05/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-05","krw":{"usd":0.00072,"jpy":0.10900,"gbp":0.00057}}
                        """, MediaType.APPLICATION_JSON));

        BigDecimal jpyRate = currencyApiClient.getRate(LocalDate.of(2026, 4, 5), "KRW", "JPY");
        BigDecimal gbpRate = currencyApiClient.getRate(LocalDate.of(2026, 4, 5), "KRW", "GBP");

        assertThat(jpyRate).isEqualByComparingTo("0.10900");
        assertThat(gbpRate).isEqualByComparingTo("0.00057");
        mockServer.verify();
    }

    @Test
    @DisplayName("오늘 날짜 환율 조회는 latest 경로를 사용해 404 위험을 줄인다")
    void getRate_whenDateIsToday_thenUsesLatestSegment() {
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-07","krw":{"usd":0.00074}}
                        """, MediaType.APPLICATION_JSON));

        BigDecimal rate = currencyApiClient.getRate(LocalDate.of(2026, 4, 7), "KRW", "USD");

        assertThat(rate).isEqualByComparingTo("0.00074");
        mockServer.verify();
    }

    @Test
    @DisplayName("날짜가 다르면 서로 다른 환율 응답을 그대로 반영한다")
    void getRate_whenDatesDiffer_thenReturnsDifferentRatesPerDate() {
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2026-04-05/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-05","krw":{"usd":0.00072}}
                        """, MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2026-04-06/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-06","krw":{"usd":0.00073}}
                        """, MediaType.APPLICATION_JSON));

        BigDecimal previousDayRate = currencyApiClient.getRate(LocalDate.of(2026, 4, 5), "KRW", "USD");
        BigDecimal issueDayRate = currencyApiClient.getRate(LocalDate.of(2026, 4, 6), "KRW", "USD");

        assertThat(previousDayRate).isEqualByComparingTo("0.00072");
        assertThat(issueDayRate).isEqualByComparingTo("0.00073");
        assertThat(previousDayRate).isNotEqualByComparingTo(issueDayRate);
        mockServer.verify();
    }

    @Test
    @DisplayName("primary 호출이 실패하면 fallback API로 재시도한다")
    void getRate_whenPrimaryFails_thenUsesFallback() {
        mockServer.expect(requestTo(
                        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@2026-04-06/v1/currencies/krw.json"))
                .andRespond(request -> {
                    throw new IllegalStateException("primary failed");
                });
        mockServer.expect(requestTo(
                        "https://2026-04-06.currency-api.pages.dev/v1/currencies/krw.json"))
                .andRespond(withSuccess("""
                        {"date":"2026-04-06","krw":{"usd":0.00073}}
                        """, new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)));

        BigDecimal rate = currencyApiClient.getRate(LocalDate.of(2026, 4, 6), "KRW", "USD");

        assertThat(rate).isEqualByComparingTo("0.00073");
        mockServer.verify();
    }

    @Test
    @DisplayName("환율 API 기능이 비활성화되면 명시적으로 예외를 던진다")
    void getRate_whenDisabled_thenThrowsException() {
        currencyApiProperties.setEnabled(false);

        assertThatThrownBy(() -> currencyApiClient.getRate(LocalDate.of(2026, 4, 6), "KRW", "USD"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("currency.api.enabled 설정이 필요합니다.");
    }
}
