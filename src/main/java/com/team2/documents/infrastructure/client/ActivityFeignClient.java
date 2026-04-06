package com.team2.documents.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.team2.documents.command.application.dto.EmailLogInternalRequest;

@FeignClient(name = "activity-service", url = "${activity.service.url:http://localhost:8083}")
public interface ActivityFeignClient {

    @PostMapping("/api/email-logs/internal")
    void createEmailLog(@RequestBody EmailLogInternalRequest request);
}
