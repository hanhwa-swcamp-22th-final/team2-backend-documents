package com.team2.documents.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthInternalUserResponse;
import com.team2.documents.command.infrastructure.client.MasterBuyerResponse;
import com.team2.documents.command.infrastructure.client.MasterFeignClient;

/**
 * AutoEmailRecipientResolver — Feign 기반 리팩토링 후 단위 테스트.
 * 이전 버전은 JdbcTemplate cross-schema SQL 을 직접 호출했으나,
 * 현재는 MasterFeignClient / AuthFeignClient 를 통한 내부 전용 엔드포인트 호출로 전환됨.
 */
@ExtendWith(MockitoExtension.class)
class AutoEmailRecipientResolverTest {

    @Mock
    private MasterFeignClient masterFeignClient;

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private AutoEmailRecipientResolver resolver;

    // ── 헬퍼 ─────────────────────────────────────────────────
    private MasterBuyerResponse buyer(Integer id, String name, String email) {
        MasterBuyerResponse b = new MasterBuyerResponse();
        b.setId(id);
        b.setBuyerName(name);
        b.setBuyerEmail(email);
        return b;
    }

    private AuthInternalUserResponse user(Integer id, String email) {
        AuthInternalUserResponse u = new AuthInternalUserResponse();
        u.setUserId(id);
        u.setUserEmail(email);
        return u;
    }

    // ── 바이어 이메일 조회 ───────────────────────────────────
    @Test
    @DisplayName("거래처 ID 로 바이어 이메일 목록을 조회한다 (blank 제외, distinct, buyerId 오름차순)")
    void findBuyerEmailsByClientId_returnsBuyerEmails() {
        when(masterFeignClient.getBuyersByClient(eq(1))).thenReturn(List.of(
                buyer(1, "Alice", "buyer1@example.com"),
                buyer(2, "Bob", "buyer2@example.com")
        ));

        List<String> emails = resolver.findBuyerEmailsByClientId(1);

        assertThat(emails).containsExactly("buyer1@example.com", "buyer2@example.com");
    }

    @Test
    @DisplayName("blank / null 이메일은 제외하고 중복은 distinct 처리한다")
    void findBuyerEmailsByClientId_filtersBlankAndDistinct() {
        when(masterFeignClient.getBuyersByClient(eq(2))).thenReturn(List.of(
                buyer(1, "A", "a@x.com"),
                buyer(2, "B", null),
                buyer(3, "C", ""),
                buyer(4, "D", "a@x.com"),
                buyer(5, "E", "e@x.com")
        ));

        List<String> emails = resolver.findBuyerEmailsByClientId(2);

        assertThat(emails).containsExactly("a@x.com", "e@x.com");
    }

    @Test
    @DisplayName("Feign 이 빈 리스트를 반환하면 빈 리스트를 반환한다")
    void findBuyerEmailsByClientId_empty() {
        when(masterFeignClient.getBuyersByClient(eq(99))).thenReturn(List.of());

        List<String> emails = resolver.findBuyerEmailsByClientId(99);

        assertThat(emails).isEmpty();
    }

    // ── 대표 바이어 이름 ─────────────────────────────────────
    @Test
    @DisplayName("대표 바이어 이름은 buyerId 가 가장 작은 바이어의 이름을 반환한다")
    void findPrimaryBuyerNameByClientId_returnsFirst() {
        when(masterFeignClient.getBuyersByClient(eq(1))).thenReturn(List.of(
                buyer(3, "Charlie", "c@x.com"),
                buyer(1, "Alice", "a@x.com"),
                buyer(2, "Bob", "b@x.com")
        ));

        assertThat(resolver.findPrimaryBuyerNameByClientId(1)).isEqualTo("Alice");
    }

    @Test
    @DisplayName("바이어가 없거나 이름이 모두 blank 면 placeholder 'Buyer' 를 반환한다")
    void findPrimaryBuyerNameByClientId_fallbackPlaceholder() {
        when(masterFeignClient.getBuyersByClient(eq(99))).thenReturn(List.of());

        assertThat(resolver.findPrimaryBuyerNameByClientId(99)).isEqualTo("Buyer");
    }

    // ── 팀 이메일 조회 ───────────────────────────────────────
    @Test
    @DisplayName("생산팀 이메일은 role=production 으로 AuthFeign 을 호출한다")
    void findProductionTeamEmails_callsAuthWithProductionRole() {
        when(authFeignClient.getUsersByRole(eq("production"), eq("active")))
                .thenReturn(List.of(
                        user(1, "prod1@example.com"),
                        user(2, "prod2@example.com")
                ));

        List<String> emails = resolver.findProductionTeamEmails();

        assertThat(emails).containsExactly("prod1@example.com", "prod2@example.com");
    }

    @Test
    @DisplayName("출하팀 이메일은 role=shipping 으로 AuthFeign 을 호출한다")
    void findShippingTeamEmails_callsAuthWithShippingRole() {
        when(authFeignClient.getUsersByRole(eq("shipping"), eq("active")))
                .thenReturn(List.of(
                        user(10, "ship@example.com")
                ));

        List<String> emails = resolver.findShippingTeamEmails();

        assertThat(emails).containsExactly("ship@example.com");
    }

    @Test
    @DisplayName("team 이메일 조회 시 blank / null 은 제외하고 userId 오름차순으로 정렬한다")
    void findTeamEmailsByRole_filtersAndSorts() {
        when(authFeignClient.getUsersByRole(eq("production"), eq("active")))
                .thenReturn(List.of(
                        user(3, "c@x.com"),
                        user(1, "a@x.com"),
                        user(2, null),
                        user(4, ""),
                        user(5, "a@x.com")
                ));

        List<String> emails = resolver.findProductionTeamEmails();

        assertThat(emails).containsExactly("a@x.com", "c@x.com");
    }
}
