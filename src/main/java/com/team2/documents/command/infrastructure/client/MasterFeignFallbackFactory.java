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
        return clientId -> {
            log.warn("[fallback] master-service getBuyersByClient({}) unavailable: {}",
                    clientId, cause != null ? cause.getMessage() : "unknown");
            return Collections.emptyList();
        };
    }
}
