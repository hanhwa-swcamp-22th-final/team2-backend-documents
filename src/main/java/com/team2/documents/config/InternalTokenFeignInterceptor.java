package com.team2.documents.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documents -> Activity 내부 호출 시 X-Internal-Token 헤더를 자동으로 주입한다.
 * Activity 서비스의 InternalApiTokenFilter 가 이 토큰을 검증한다.
 *
 * 운영에서는 docker-compose / k8s secret 으로 INTERNAL_API_TOKEN 환경변수를 주입할 것.
 * 토큰이 비어 있으면 (개발 환경) 헤더를 추가하지 않는다.
 */
@Configuration
public class InternalTokenFeignInterceptor {

    @Value("${internal.api.token:}")
    private String internalToken;

    @Bean
    public RequestInterceptor internalTokenRequestInterceptor() {
        return (RequestTemplate template) -> {
            // /api/email-logs/internal/** 호출에만 헤더 주입
            String path = template.url();
            if (path != null && path.contains("/api/email-logs/internal")
                    && internalToken != null && !internalToken.isBlank()) {
                template.header("X-Internal-Token", internalToken);
            }
        };
    }
}
