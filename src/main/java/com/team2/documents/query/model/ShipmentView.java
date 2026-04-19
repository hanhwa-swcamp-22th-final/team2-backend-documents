package com.team2.documents.query.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentView {
    private Long shipmentId;
    private String poId;
    private String shipmentOrderId;
    private String shipmentStatus;
    // 프런트 출하현황 그리드 표시용 enrich 필드 (shipments.* + shipment_orders snapshot)
    private String clientName;
    private String country;
    private String managerName;
    private String itemName;
    // Step E — PO.po_items_snapshot 을 JOIN 으로 가져와 Shipment 상세의 품목 테이블 채움.
    // JSON 문자열. 프론트가 parse.
    private String itemsSnapshot;
    private LocalDate requestDate;
    private LocalDate dueDate;
}
