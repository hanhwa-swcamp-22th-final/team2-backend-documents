package com.team2.documents.command.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthUserResponse {
    private Long userId;

    @JsonAlias({"name", "userName", "username", "memberName", "employeeName"})
    private String name;
    private AuthPositionResponse position;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthPositionResponse {
        private Integer positionLevel;
    }
}
