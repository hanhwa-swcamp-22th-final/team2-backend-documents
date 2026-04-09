package com.team2.documents.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Feign 요청의 서비스 간 통신 인증 정책
 * ─────────────────────────────────────────────────────────
 * [원칙]
 *   1) 사용자 트리거 호출(일반 API) → 현재 SecurityContext 의 Bearer JWT 를 그대로 전파
 *      → 수신 서비스에서 동일한 사용자 권한으로 RBAC 평가 (감사 추적 + defense in depth)
 *   2) 시스템 호출(내부 전용 API) → Bearer 를 보내지 않고 X-Internal-Token 만 전송
 *      (주입은 {@link InternalTokenFeignInterceptor} 에서 담당)
 *      → 사용자 컨텍스트와 무관하게 동작, 스케줄러/배치/이벤트에서도 안전
 *
 * 본 Bean 은 위 원칙 1을 구현한다. 원칙 2에 해당하는 경로는 아래 {@link #isInternalPath(String)}
 * 로 식별하며 Bearer 전파를 생략한다.
 */
@Configuration
public class FeignAuthorizationConfig {

    @Bean
    public RequestInterceptor bearerTokenForwardingInterceptor() {
        return template -> {
            // 내부 전용 경로에는 Bearer 를 붙이지 않는다 (시스템 호출이므로 사용자 컨텍스트 불필요).
            if (isInternalPath(template.url())) {
                return;
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                template.header("Authorization", "Bearer " + jwt.getTokenValue());
            }
        };
    }

    /**
     * 내부 전용 엔드포인트 경로 식별자.
     * 관례: 경로에 {@code /internal} 세그먼트가 포함된 것.
     * 예: {@code /api/email-logs/internal}
     */
    static boolean isInternalPath(String url) {
        return url != null && url.contains("/internal");
    }
}
