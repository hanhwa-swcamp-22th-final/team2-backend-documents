package com.team2.documents.command.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",
        url = "${auth.service.url:http://localhost:8011}",
        fallbackFactory = AuthFeignFallbackFactory.class
)
public interface AuthFeignClient {

    @GetMapping("/api/users/{userId}")
    AuthUserResponse getUser(@PathVariable("userId") Long userId);
}
