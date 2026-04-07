package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthUserResponse;

@ExtendWith(MockitoExtension.class)
class UserSnapshotServiceTest {

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private UserSnapshotService userSnapshotService;

    @Test
    @DisplayName("사용자 이름이 있으면 표시 이름으로 반환한다")
    void resolveRequesterDisplayName_whenUserExists_thenReturnsName() {
        AuthUserResponse response = new AuthUserResponse();
        response.setName("김영업");
        when(authFeignClient.getUser(2L)).thenReturn(response);

        String result = userSnapshotService.resolveRequesterDisplayName(2L);

        assertEquals("김영업", result);
    }

    @Test
    @DisplayName("사용자 조회에 실패하면 사용자 ID 문자열을 반환한다")
    void resolveRequesterDisplayName_whenAuthCallFails_thenReturnsUserIdString() {
        when(authFeignClient.getUser(2L)).thenThrow(new RuntimeException("boom"));

        String result = userSnapshotService.resolveRequesterDisplayName(2L);

        assertEquals("2", result);
    }
}
