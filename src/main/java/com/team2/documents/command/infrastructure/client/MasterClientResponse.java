package com.team2.documents.command.infrastructure.client;

/**
 * Master /api/clients/internal/{id} 응답 슬림 DTO.
 * 원본 Master ClientResponse 는 필드가 많지만 Documents 쪽에서는 PO 스냅샷에 필요한
 * paymentTermName / portName / clientEmail 만 필요하므로 최소로 선언.
 * Jackson 이 unknown 필드는 기본 무시.
 */
public record MasterClientResponse(
        Integer id,
        Integer clientId,
        String clientCode,
        String clientName,
        String clientNameKr,
        String clientEmail,
        Integer paymentTermId,
        String paymentTermName,
        Integer portId,
        String portName,
        String currencyName
) {
    public Integer resolveId() {
        return id != null ? id : clientId;
    }
}
