package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthUserResponse;

@Service
public class UserSnapshotService {

    private final AuthFeignClient authFeignClient;

    public UserSnapshotService(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    public String resolveRequesterDisplayName(Long userId) {
        if (userId == null) {
            return "";
        }
        try {
            AuthUserResponse response = authFeignClient.getUser(userId);
            if (response != null && response.getName() != null && !response.getName().isBlank()) {
                return response.getName();
            }
        } catch (RuntimeException ignored) {
            return String.valueOf(userId);
        }
        return String.valueOf(userId);
    }
}
