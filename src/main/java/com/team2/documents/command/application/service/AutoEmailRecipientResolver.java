package com.team2.documents.command.application.service;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthInternalUserResponse;
import com.team2.documents.command.infrastructure.client.MasterBuyerResponse;
import com.team2.documents.command.infrastructure.client.MasterFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 자동 메일 발송 시 수신자(바이어/내부 팀원) 이메일·이름을 해소한다.
 *
 * <p>[리팩토링 전] {@code team2_master.buyers}, {@code team2_auth.users} 를 cross-schema SQL 로
 * 직접 조회했으나 이는 MSA 원칙 위반이었다.
 *
 * <p>[리팩토링 후] {@link MasterFeignClient}, {@link AuthFeignClient} 의 내부 전용 엔드포인트
 * ({@code /api/buyers/internal/by-client/{id}}, {@code /api/users/internal/by-role}) 를 호출한다.
 * Feign 호출 시 {@code InternalTokenFeignInterceptor} 가 X-Internal-Token 헤더를 자동 주입하며,
 * Gateway 에서는 {@code /api/**\/internal/**} 경로를 denyAll 로 외부 차단한다.
 *
 * <p>Fallback: Feign 장애 시 fallbackFactory 가 빈 목록을 반환하므로 본 클래스는 추가 방어 불필요.
 */
@Service
public class AutoEmailRecipientResolver {

    private static final Logger log = LoggerFactory.getLogger(AutoEmailRecipientResolver.class);

    private static final String DEFAULT_ACTIVE_STATUS = "active";
    private static final String ROLE_PRODUCTION = "production";
    private static final String ROLE_SHIPPING = "shipping";

    private final MasterFeignClient masterFeignClient;
    private final AuthFeignClient authFeignClient;

    public AutoEmailRecipientResolver(MasterFeignClient masterFeignClient,
                                      AuthFeignClient authFeignClient) {
        this.masterFeignClient = masterFeignClient;
        this.authFeignClient = authFeignClient;
    }

    /**
     * 특정 거래처의 바이어 이메일 목록 (중복 제거, blank 제외, buyerId 오름차순).
     */
    public List<String> findBuyerEmailsByClientId(Integer clientId) {
        return loadBuyerRecipientsByClientId(clientId).emails();
    }

    /**
     * 특정 거래처의 대표 바이어 이름 (가장 먼저 등록된 바이어).
     * 결과가 없으면 placeholder "Buyer" 반환 (기존 동작 유지).
     */
    public String findPrimaryBuyerNameByClientId(Integer clientId) {
        return loadBuyerRecipientsByClientId(clientId).primaryName();
    }

    @Cacheable(cacheNames = "autoMailBuyerRecipients", key = "#clientId")
    public BuyerRecipients findBuyerRecipientsByClientId(Integer clientId) {
        return loadBuyerRecipientsByClientId(clientId);
    }

    private BuyerRecipients loadBuyerRecipientsByClientId(Integer clientId) {
        List<MasterBuyerResponse> buyers = safeList(masterFeignClient.getBuyersByClient(clientId));
        List<MasterBuyerResponse> sortedBuyers = buyers.stream()
                .sorted(Comparator.comparing(MasterBuyerResponse::getId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        List<String> emails = sortedBuyers.stream()
                .map(MasterBuyerResponse::getBuyerEmail)
                .filter(AutoEmailRecipientResolver::isNotBlank)
                .distinct()
                .toList();
        String primaryName = sortedBuyers.stream()
                .map(MasterBuyerResponse::getBuyerName)
                .filter(AutoEmailRecipientResolver::isNotBlank)
                .findFirst()
                .orElse("Buyer");
        return new BuyerRecipients(emails, primaryName);
    }

    /** 생산팀(production 역할) 재직 사용자의 이메일 목록. */
    @Cacheable(cacheNames = "autoMailProductionTeamEmails")
    public List<String> findProductionTeamEmails() {
        return findTeamEmailsByRole(ROLE_PRODUCTION);
    }

    /** 출하팀(shipping 역할) 재직 사용자의 이메일 목록. */
    @Cacheable(cacheNames = "autoMailShippingTeamEmails")
    public List<String> findShippingTeamEmails() {
        return findTeamEmailsByRole(ROLE_SHIPPING);
    }

    private List<String> findTeamEmailsByRole(String role) {
        List<AuthInternalUserResponse> users =
                safeList(authFeignClient.getUsersByRole(role, DEFAULT_ACTIVE_STATUS));
        return users.stream()
                .sorted(Comparator.comparing(AuthInternalUserResponse::getUserId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(AuthInternalUserResponse::getUserEmail)
                .filter(AutoEmailRecipientResolver::isNotBlank)
                .distinct()
                .toList();
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    public record BuyerRecipients(List<String> emails, String primaryName) {
        public BuyerRecipients {
            emails = emails == null ? List.of() : List.copyOf(emails);
            primaryName = isNotBlank(primaryName) ? primaryName : "Buyer";
        }
    }
}
