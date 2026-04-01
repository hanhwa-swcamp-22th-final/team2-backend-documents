package com.team2.documents.command.domain.repository;

import java.util.Optional;

import com.team2.documents.command.domain.entity.enums.PositionLevel;

public interface UserPositionRepository {
    Optional<PositionLevel> findPositionLevelByUserId(Long userId);
}
