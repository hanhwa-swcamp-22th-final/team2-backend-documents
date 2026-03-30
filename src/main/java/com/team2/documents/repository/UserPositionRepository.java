package com.team2.documents.repository;

import java.util.Optional;

import com.team2.documents.entity.enums.PositionLevel;

public interface UserPositionRepository {

    Optional<PositionLevel> findPositionLevelByUserId(Long userId);
}
