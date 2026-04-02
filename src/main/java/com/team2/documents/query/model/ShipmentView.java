package com.team2.documents.query.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentView {
    private Long shipmentId;
    private String poId;
    private String shipmentStatus;
}
