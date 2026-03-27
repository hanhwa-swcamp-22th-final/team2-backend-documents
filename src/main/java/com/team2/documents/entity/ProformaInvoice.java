package com.team2.documents.entity;

public class ProformaInvoice {

    private final String piId;
    private ProformaInvoiceStatus status;

    public ProformaInvoice(String piId, ProformaInvoiceStatus status) {
        this.piId = piId;
        this.status = status;
    }

    public String getPiId() {
        return piId;
    }

    public ProformaInvoiceStatus getStatus() {
        return status;
    }

    public void requestRegistration() {
        if (!ProformaInvoiceStatus.DRAFT.equals(status)) {
            throw new IllegalStateException("초안 상태의 PI만 등록 요청할 수 있습니다.");
        }
        this.status = ProformaInvoiceStatus.APPROVAL_PENDING;
    }

    public void confirmRegistration() {
        if (!ProformaInvoiceStatus.DRAFT.equals(status)) {
            throw new IllegalStateException("초안 상태의 PI만 즉시 확정할 수 있습니다.");
        }
        this.status = ProformaInvoiceStatus.CONFIRMED;
    }

    public void approve() {
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(status)) {
            throw new IllegalStateException("결재대기 상태의 PI만 승인할 수 있습니다.");
        }
        this.status = ProformaInvoiceStatus.CONFIRMED;
    }

    public void reject() {
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(status)) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        this.status = ProformaInvoiceStatus.REJECTED;
    }
}
