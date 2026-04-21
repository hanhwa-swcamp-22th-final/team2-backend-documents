package com.team2.documents.command.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Documents → Master 내부 전용 호출.
 * X-Internal-Token 헤더는 InternalTokenFeignInterceptor 가 /internal 경로 감지 후 자동 주입.
 */
@FeignClient(
        name = "master-service",
        url = "${master.service.url:http://localhost:8012}",
        fallbackFactory = MasterFeignFallbackFactory.class
)
public interface MasterFeignClient {

    /**
     * 거래처에 소속된 바이어 목록 조회 (플랫 List 응답).
     * Master 서비스의 {@code GET /api/buyers/internal/by-client/{clientId}} 엔드포인트.
     */
    @GetMapping("/api/buyers/internal/by-client/{clientId}")
    List<MasterBuyerResponse> getBuyersByClient(@PathVariable("clientId") Integer clientId);
}
