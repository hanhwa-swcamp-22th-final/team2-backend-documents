package com.team2.documents.entity;

public class PurchaseOrder {

    private final String poId;
    private PurchaseOrderStatus status;

    public PurchaseOrder(String poId) {
        this.poId = poId;
        this.status = PurchaseOrderStatus.DRAFT;
    }

    public PurchaseOrder(String poId, PurchaseOrderStatus status) {
        this.poId = poId;
        this.status = status;
    }

    public String getPoId() {
        return poId;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public static PurchaseOrderStatus determineInitialStatus(PositionLevel positionLevel) {
        if (PositionLevel.MANAGER.equals(positionLevel)) {
            return PurchaseOrderStatus.CONFIRMED;
        }
        return PurchaseOrderStatus.APPROVAL_PENDING;
    }

    public void requestRegistration() {
        if (!PurchaseOrderStatus.DRAFT.equals(status)) {
            throw new IllegalStateException("초안 상태의 PO만 등록 요청할 수 있습니다.");
        }
        this.status = PurchaseOrderStatus.APPROVAL_PENDING;
    }

    public void validateModifiable(ShipmentStatus shipmentStatus) {
        if (ShipmentStatus.COMPLETED.equals(shipmentStatus)) {
            throw new IllegalStateException("출하완료 상태의 PO는 수정할 수 없습니다.");
        }
    }

    public void validateDeletable(ShipmentStatus shipmentStatus) {
        if (ShipmentStatus.COMPLETED.equals(shipmentStatus)) {
            throw new IllegalStateException("출하완료 상태의 PO는 삭제할 수 없습니다.");
        }
    }
}
