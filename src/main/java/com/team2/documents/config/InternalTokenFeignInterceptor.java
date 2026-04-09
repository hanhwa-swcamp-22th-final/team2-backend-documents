package com.team2.documents.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 내부 전용 Feign 호출에 X-Internal-Token 헤더를 자동 주입한다.
 * 관례: 경로에 {@code /internal} 세그먼트가 포함된 호출을 시스템 호출로 간주한다.
 *
 * 현재 주요 대상:
 *   - Documents → Activity {@code /api/email-logs/internal} (메일 발송 후 이력 기록)
 *
 * 수신 측은 각 서비스의 InternalApiTokenFilter(또는 동등 필터)가 헤더 값을 검증한다.
 * 운영에서는 docker-compose / k8s secret 으로 INTERNAL_API_TOKEN 환경변수 주입.
 * 토큰이 비어 있으면 (개발 환경) 헤더를 추가하지 않는다 — 수신 측도 blank 일 때는 통과시킴.
 *
 * Bearer JWT 전파는 {@link FeignAuthorizationConfig} 에서 담당하며, 내부 경로에는
 * 전파하지 않도록 분리돼 있어 시스템/사용자 채널이 명확히 구분된다.
 */
@Configuration
public class InternalTokenFeignInterceptor {

    @Value("${internal.api.token:}")
    private String internalToken;

    @Bean
    public RequestInterceptor internalTokenRequestInterceptor() {
        return (RequestTemplate template) -> {
            if (!isInternalPath(template.url())) {
                return;
            }
            if (internalToken == null || internalToken.isBlank()) {
                return;
            }
            template.header("X-Internal-Token", internalToken);
        };
    }

    /** 내부 전용 경로 식별자. FeignAuthorizationConfig 와 동일 규칙. */
    static boolean isInternalPath(String url) {
        return url != null && url.contains("/internal");
    }
}
