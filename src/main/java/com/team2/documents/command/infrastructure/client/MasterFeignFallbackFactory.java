package com.team2.documents.command.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MasterFeignFallbackFactory implements FallbackFactory<MasterFeignClient> {

    private static final Logger log = LoggerFactory.getLogger(MasterFeignFallbackFactory.class);

    @Override
    public MasterFeignClient create(Throwable cause) {
        final String reason = cause != null ? cause.getMessage() : "unknown";
        return new MasterFeignClient() {
            @Override
            public List<MasterBuyerResponse> getBuyersByClient(Integer clientId) {
                log.warn("[fallback] master getBuyersByClient({}) unavailable: {}", clientId, reason);
                return Collections.emptyList();
            }

            @Override
            public MasterClientResponse getClientById(Integer clientId) {
                log.warn("[fallback] master getClientById({}) unavailable: {}", clientId, reason);
                // paymentTerm / port 스냅샷을 못 넣은 채로 PO 생성이 진행되도록 null 필드 응답.
                return new MasterClientResponse(clientId, clientId, null, null, null, null, null, null, null, null, null);
            }
        };
    }
}
