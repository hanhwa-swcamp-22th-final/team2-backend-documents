package com.team2.documents.command.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Master 서비스 내부 엔드포인트 {@code /api/buyers/internal/by-client/{clientId}} 의 응답 원소.
 * Master 의 BuyerResponse 와 필드 매칭.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterBuyerResponse {
    private Integer id;
    private Integer clientId;
    private String clientName;
    private String buyerName;
    private String buyerPosition;
    private String buyerEmail;
    private String buyerTel;
}
