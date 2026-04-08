package com.team2.documents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceItemCreateRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.ProformaInvoiceItem;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProformaInvoiceExchangeRateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM docs_revision");
        jdbcTemplate.execute("DELETE FROM pi_items");
        jdbcTemplate.execute("DELETE FROM proforma_invoices");
        jdbcTemplate.execute("DELETE FROM document_number_sequences");
    }

    @Test
    @DisplayName("PI 생성 API는 프론트에서 전달한 환율로 KRW 품목단가를 외화로 환산해 저장한다")
    @Transactional
    void createProformaInvoice_convertsKrwUnitPriceUsingFrontendExchangeRate() throws Exception {
        LocalDate issueDate = LocalDate.of(2026, 4, 6);
        BigDecimal exchangeRate = new BigDecimal("0.00073");

        ProformaInvoiceCreateRequest request = new ProformaInvoiceCreateRequest(
                "PI260900",
                issueDate,
                10,
                1,
                2L,
                LocalDate.of(2026, 4, 20),
                "FOB",
                "Busan",
                null,
                "ABC Trading",
                "Seoul",
                "KR",
                "USD",
                exchangeRate,
                "Kim",
                2L,
                List.of(new ProformaInvoiceItemCreateRequest(
                        100,
                        "Bolt",
                        2,
                        "EA",
                        new BigDecimal("10000"),
                        null,
                        "urgent"
                ))
        );

        mockMvc.perform(post("/api/proforma-invoices")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.piId").value("PI260900"));

        ProformaInvoice saved = proformaInvoiceRepository.findByPiCode("PI260900").orElseThrow();
        ProformaInvoiceItem savedItem = saved.getItems().getFirst();

        BigDecimal expectedUnitPrice = new BigDecimal("10000").multiply(exchangeRate).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal expectedTotalAmount = new BigDecimal("20000").multiply(exchangeRate).setScale(2, java.math.RoundingMode.HALF_UP);

        assertThat(savedItem.getUnitPrice()).isEqualByComparingTo(expectedUnitPrice);
        assertThat(savedItem.getAmount()).isEqualByComparingTo(expectedTotalAmount);
        assertThat(saved.getTotalAmount()).isEqualByComparingTo(expectedTotalAmount);
        assertThat(savedItem.getUnitPrice()).isNotEqualByComparingTo(new BigDecimal("10000"));
        assertThat(saved.getCurrencyCode()).isEqualTo("USD");
    }
}
