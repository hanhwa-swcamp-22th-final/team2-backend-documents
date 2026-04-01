package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthUserResponse;
import com.team2.documents.command.domain.entity.enums.PositionLevel;

@Component
public class UserPositionRepositoryImpl implements UserPositionRepository {

    private final AuthFeignClient authFeignClient;

    public UserPositionRepositoryImpl(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public Optional<PositionLevel> findPositionLevelByUserId(Long userId) {
        AuthUserResponse user = authFeignClient.getUser(userId);
        if (user == null || user.getPosition() == null) {
            return Optional.empty();
        }
        PositionLevel level = user.getPosition().getPositionLevel() == 1
                ? PositionLevel.MANAGER
                : PositionLevel.STAFF;
        return Optional.of(level);
    }
}
