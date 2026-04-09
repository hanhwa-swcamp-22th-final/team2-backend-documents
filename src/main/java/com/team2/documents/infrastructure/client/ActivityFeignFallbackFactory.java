package com.team2.documents.infrastructure.client;

import com.team2.documents.command.application.dto.EmailLogInternalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ActivityFeignFallbackFactory implements FallbackFactory<ActivityFeignClient> {

    private static final Logger log = LoggerFactory.getLogger(ActivityFeignFallbackFactory.class);

    @Override
    public ActivityFeignClient create(Throwable cause) {
        return new ActivityFeignClient() {
            @Override
            public void createEmailLog(EmailLogInternalRequest request) {
                log.warn("[fallback] activity-service createEmailLog unavailable: {}",
                        cause != null ? cause.getMessage() : "unknown");
                // Fire-and-forget: 활동 로그 기록 실패는 본 흐름을 막지 않음
            }
        };
    }
}
