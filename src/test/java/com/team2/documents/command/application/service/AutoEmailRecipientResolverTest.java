package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class AutoEmailRecipientResolverTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("거래처 ID로 바이어 이메일 목록을 조회한다")
    void findBuyerEmailsByClientId_returnsBuyerEmails() {
        AutoEmailRecipientResolver resolver = new AutoEmailRecipientResolver(jdbcTemplate);
        when(jdbcTemplate.queryForList(any(String.class), eq(String.class), eq(1)))
                .thenReturn(List.of("buyer1@example.com", "buyer2@example.com"));

        List<String> emails = resolver.findBuyerEmailsByClientId(1);

        assertEquals(List.of("buyer1@example.com", "buyer2@example.com"), emails);
    }

    @Test
    @DisplayName("생산팀 이메일은 production 역할 사용자로 조회한다")
    void findProductionTeamEmails_returnsProductionEmails() {
        AutoEmailRecipientResolver resolver = new AutoEmailRecipientResolver(jdbcTemplate);
        when(jdbcTemplate.queryForList(any(String.class), eq(String.class), eq("production")))
                .thenReturn(List.of("prod1@example.com"));

        List<String> emails = resolver.findProductionTeamEmails();

        assertEquals(List.of("prod1@example.com"), emails);
    }
}
