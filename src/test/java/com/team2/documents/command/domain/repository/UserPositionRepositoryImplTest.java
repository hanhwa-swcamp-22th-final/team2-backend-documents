package com.team2.documents.command.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthUserResponse;

@ExtendWith(MockitoExtension.class)
class UserPositionRepositoryImplTest {

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private UserPositionRepositoryImpl userPositionRepository;

    @Test
    @DisplayName("직급 레벨이 1인 사용자는 MANAGER를 반환한다")
    void findPositionLevelByUserId_whenPositionLevelIs1_thenReturnsManager() {
        // given
        AuthUserResponse response = new AuthUserResponse();
        AuthUserResponse.AuthPositionResponse position = new AuthUserResponse.AuthPositionResponse();
        position.setPositionLevel(1);
        response.setPosition(position);
        when(authFeignClient.getUser(1L)).thenReturn(response);

        // when
        Optional<PositionLevel> result = userPositionRepository.findPositionLevelByUserId(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals(PositionLevel.MANAGER, result.get());
    }

    @Test
    @DisplayName("직급 레벨이 1이 아닌 사용자는 STAFF를 반환한다")
    void findPositionLevelByUserId_whenPositionLevelIsNot1_thenReturnsStaff() {
        // given
        AuthUserResponse response = new AuthUserResponse();
        AuthUserResponse.AuthPositionResponse position = new AuthUserResponse.AuthPositionResponse();
        position.setPositionLevel(2);
        response.setPosition(position);
        when(authFeignClient.getUser(2L)).thenReturn(response);

        // when
        Optional<PositionLevel> result = userPositionRepository.findPositionLevelByUserId(2L);

        // then
        assertTrue(result.isPresent());
        assertEquals(PositionLevel.STAFF, result.get());
    }

    @Test
    @DisplayName("사용자 정보가 null이면 빈 Optional을 반환한다")
    void findPositionLevelByUserId_whenUserIsNull_thenReturnsEmpty() {
        // given
        when(authFeignClient.getUser(99L)).thenReturn(null);

        // when
        Optional<PositionLevel> result = userPositionRepository.findPositionLevelByUserId(99L);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("사용자의 직급 정보가 null이면 빈 Optional을 반환한다")
    void findPositionLevelByUserId_whenPositionIsNull_thenReturnsEmpty() {
        // given
        AuthUserResponse response = new AuthUserResponse();
        response.setPosition(null);
        when(authFeignClient.getUser(98L)).thenReturn(response);

        // when
        Optional<PositionLevel> result = userPositionRepository.findPositionLevelByUserId(98L);

        // then
        assertTrue(result.isEmpty());
    }
}
