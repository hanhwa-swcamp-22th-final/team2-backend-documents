package com.team2.documents.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthUserResponse {
    private Long userId;
    private AuthPositionResponse position;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthPositionResponse {
        private Integer positionLevel;
    }
}
